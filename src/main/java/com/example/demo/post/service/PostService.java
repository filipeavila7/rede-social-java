package com.example.demo.post.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.exeptions.tag.TagConflictException;
import com.example.demo.feed.interest.entity.UserInterest;
import com.example.demo.feed.interest.repository.UserInterestRepository;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.post.dto.PostRequest;
import com.example.demo.post.dto.PostDetaisResponse;
import com.example.demo.tag.entity.Tag;
import com.example.demo.post.dto.PostResponse;
import com.example.demo.post.mapper.PostMapper;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.tag.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.post.entity.Post;
import com.example.demo.user.entity.User;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;
    private final GlobalHelperService globalHelperService;
    private final UserInterestRepository userInterestRepository;

    private static final int CANDIDATE_POOL_SIZE = 500;
    private static final int WINDOW_DAYS = 30;
    private static final double INTEREST_WEIGHT = 0.7;
    private static final double POPULARITY_WEIGHT = 0.2;
    private static final double RECENCY_WEIGHT = 0.1;



        // ========== GET ==========

        public Page<PostDetaisResponse> getFeed(int page, int size) {
            User loggedUser = globalHelperService.getLoggedUser();

            List<Post> candidates = fetchCandidatePool();

            List<ScoredPost> scored = loggedUser != null
                    ? scorePersonalized(candidates, loggedUser)
                    : scoreGlobal(candidates);

            List<Post> ranked = applyDiversity(scored);

            return paginate(ranked, page, size, loggedUser);
        }

        private List<Post> fetchCandidatePool() {
            LocalDateTime since = LocalDateTime.now().minusDays(WINDOW_DAYS);
            List<Post> candidates = postRepository.findCandidatePosts(
                    since, PageRequest.of(0, CANDIDATE_POOL_SIZE));
            return postRepository.findWithTagsFetched(candidates);
        }

        // ---------- ranking para usuário autenticado ----------

        private List<ScoredPost> scorePersonalized(List<Post> candidates, User user) {
            Map<Long, Double> tagScores = loadUserTagScores(user, candidates);
            double maxInterest = tagScores.values().stream().mapToDouble(v -> v).max().orElse(1.0);

            double now = System.currentTimeMillis();

            return candidates.stream()
                    .map(post -> {
                        double interestScore = normalizedInterestScore(post, tagScores, maxInterest);
                        double popularityScore = normalizedPopularity(post);
                        double recencyScore = normalizedRecency(post, now);

                        double finalScore =
                                INTEREST_WEIGHT * interestScore
                                        + POPULARITY_WEIGHT * popularityScore
                                        + RECENCY_WEIGHT * recencyScore;

                        return new ScoredPost(post, finalScore);
                    })
                    .sorted(Comparator.comparingDouble(ScoredPost::score).reversed())
                    .toList();
        }

        // busca em lote o score de interesse do usuário só pras tags que aparecem no pool
        private Map<Long, Double> loadUserTagScores(User user, List<Post> candidates) {
            List<Long> tagIds = candidates.stream()
                    .flatMap(p -> p.getTags().stream())
                    .map(Tag::getId)
                    .distinct()
                    .toList();

            return userInterestRepository.findByUserIdAndTagIdIn(user.getId(), tagIds)
                    .stream()
                    .collect(Collectors.toMap(ui -> ui.getTag().getId(), UserInterest::getScore));
        }

        private double normalizedInterestScore(Post post, Map<Long, Double> tagScores, double max) {
            if (max == 0) return 0;
            double sum = post.getTags().stream()
                    .mapToDouble(tag -> tagScores.getOrDefault(tag.getId(), 0.0))
                    .sum();
            return Math.min(1.0, sum / max);
        }

        // ---------- ranking global (visitante) ----------

        private List<ScoredPost> scoreGlobal(List<Post> candidates) {
            double now = System.currentTimeMillis();

            return candidates.stream()
                    .map(post -> {
                        double popularityScore = normalizedPopularity(post);
                        double recencyScore = normalizedRecency(post, now);
                        double randomness = ThreadLocalRandom.current().nextDouble(0, 0.1);

                        double finalScore = 0.6 * popularityScore + 0.3 * recencyScore + randomness;

                        return new ScoredPost(post, finalScore);
                    })
                    .sorted(Comparator.comparingDouble(ScoredPost::score).reversed())
                    .toList();
        }

        // ---------- fatores compartilhados ----------

        private double normalizedPopularity(Post post) {
            long likes = post.getLikesCount(); // ou likeRepository.countByPost(post) se não for contador denormalizado
            long comments = post.getCommentsCount();
            double raw = likes + comments * 1.5;
            return Math.min(1.0, raw / 100.0); // 100 = teto arbitrário, ajustar com dados reais
        }

        private double normalizedRecency(Post post, double now) {
            long ageHours = Duration.between(post.getCreatedAt(), LocalDateTime.now()).toHours();
            return Math.max(0, 1.0 - (ageHours / (24.0 * WINDOW_DAYS)));
        }

        // ---------- diversidade (re-rank guloso) ----------

        private List<Post> applyDiversity(List<ScoredPost> scored) {
            List<Post> result = new ArrayList<>();
            Map<Long, Integer> consecutiveTagCount = new HashMap<>();
            int maxConsecutive = 2;

            List<ScoredPost> remaining = new ArrayList<>(scored);

            while (!remaining.isEmpty()) {
                ScoredPost next = remaining.stream()
                        .filter(sp -> !violatesDiversity(sp.post(), consecutiveTagCount, maxConsecutive))
                        .findFirst()
                        .orElse(remaining.get(0)); // se todos violarem, aceita o melhor mesmo assim

                result.add(next.post());
                remaining.remove(next);
                updateConsecutiveCount(next.post(), consecutiveTagCount);
            }

            return result;
        }

        private boolean violatesDiversity(Post post, Map<Long, Integer> counts, int max) {
            return post.getTags().stream()
                    .anyMatch(tag -> counts.getOrDefault(tag.getId(), 0) >= max);
        }

        private void updateConsecutiveCount(Post post, Map<Long, Integer> counts) {
            // zera tags que não estão nesse post, incrementa as que estão
            Set<Long> postTagIds = post.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
            counts.replaceAll((tagId, count) -> postTagIds.contains(tagId) ? count + 1 : 0);
            for (Long tagId : postTagIds) {
                counts.putIfAbsent(tagId, 1);
            }
        }

        // ---------- paginação sobre o pool já ranqueado ----------

        private Page<PostDetaisResponse> paginate(List<Post> ranked, int page, int size, User loggedUser) {
            Pageable pageable = PageRequest.of(page, size);

            int start = Math.toIntExact(pageable.getOffset());
            int end = Math.min(start + pageable.getPageSize(), ranked.size());

            List<Post> pageContent = start >= ranked.size() ? List.of() : ranked.subList(start, end);

            Long userId = loggedUser != null ? loggedUser.getId() : null;

            return new PageImpl<>(pageContent, pageable, ranked.size())
                    .map(post -> postMapper.toPostDetaisResponse(post, userId));
        }

        private record ScoredPost(Post post, double score) {}



    // posts do usuario logado
    public Page<PostDetaisResponse> getMyPosts(Pageable pageable) {
        User user = globalHelperService.getLoggedUser();

        return postRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(post -> postMapper.toPostDetaisResponse(post, user.getId()));
    }


    // buscar post pelo id
    public PostDetaisResponse getPostById(Long postId) {
        // pegar usuario logado
        User loggedUser = globalHelperService.getLoggedUser();

        // verifica se o post existe
        Post post = globalHelperService.findPostById(postId);

        // retorna o post response
        return postMapper.toPostDetaisResponse(post, loggedUser.getId());
    }


    // posts de outro usuario
    public Page<PostDetaisResponse> getPostsByUserName(String userName, Pageable pageable) {
        User loggedUser = globalHelperService.getLoggedUser();

        return postRepository
                .findByUserUserNameOrderByCreatedAtDesc(userName, pageable)
                .map(post -> postMapper.toPostDetaisResponse(post, loggedUser.getId()));
    }

    // quantidade de posts
    public long getPostsCountByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }

    // stats isoladas
    // TODO - POSSÍVEL METODO REDUNDANTE - a quantidade de likes e comentarios ja estão sendo retornados no dto
    // de postDetais
    public Map<String, Long> getPostStats(Long postId) {
        long likes = likeRepository.countByPostId(postId);
        long comments = commentRepository.countByPostId(postId);

        Map<String, Long> stats = new HashMap<>();
        stats.put("likes", likes);
        stats.put("comments", comments);
        return stats;
    }


    public Page<PostDetaisResponse> searchPosts(String termo, Pageable pageable) {

        String busca = termo.trim();

        if (busca.isEmpty()) {
            return Page.empty();
        }

        User loggedUser = globalHelperService.getLoggedUser();

        return postRepository
                .findDistinctByTitleContainingIgnoreCaseOrTagsNameContainingIgnoreCaseOrderByCreatedAtDesc(
                        busca,
                        busca,
                        pageable
                )
                .map(post -> postMapper.toPostDetaisResponse(post, loggedUser.getId()));
    }


    //sugestões
    public List<String> searchPostSuggestions(String termo) {

        if (termo == null || termo.trim().isEmpty()) {
            return List.of();
        }

        List<Post> posts = postRepository
                .findTop8DistinctByTitleContainingIgnoreCaseOrTagsNameContainingIgnoreCaseOrderByCreatedAtDesc(
                        termo.trim(),
                        termo.trim()
                );

        return posts.stream()
                .flatMap(post -> {
                    List<String> resultados = new ArrayList<>();

                    if (post.getTitle() != null) {
                        resultados.add(post.getTitle());
                    }

                    post.getTags().forEach(tag -> resultados.add(tag.getName()));

                    return resultados.stream();
                })
                .filter(Objects::nonNull)
                .filter(texto -> texto.toLowerCase().contains(termo.toLowerCase()))
                .distinct()
                .limit(6)
                .toList();
    }



    // ========== POST ==========

    // criar post
    @Transactional
    public PostResponse createPost(PostRequest request) {
        // pega o user logado
        User user = globalHelperService.getLoggedUser();

        // verifica se os ids das tags passadas existem
        List<Tag> tags = tagRepository.findAllById(request.tagIds());

        if (tags.size() != request.tagIds().size()) {
            throw new TagConflictException("Tag não encontrada");
        }

        // verifica se tem no máximo 3 tags por post
        if (request.tagIds().size() > 3) {
            throw new TagConflictException("É permitido no máximo 3 tags");
        }

        // verificar se não estão repetidas
        Set<Long> uniqueTags = new HashSet<>(request.tagIds());

        if (uniqueTags.size() != request.tagIds().size()) {
            throw new TagConflictException("Tags repetidas");
        }

        // cria o post
        Post post = new Post();
        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setImageUrl(request.imageUrl());
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(user);
        post.setTags(tags);

        return postMapper.toPostResponse(postRepository.save(post));
    }

    // ========== PUT ==========

    // editar post
    // TODO - esse metodo possivelmente sera removido
    public Post updatePost(Long postId, Post postAtualizado) {
        // busca o post
        Post post = globalHelperService.findPostById(postId);

        // pega o user logado
        User loggedUser = globalHelperService.getLoggedUser();

        // verifica se o user logado é o dono do post
        globalHelperService.validatePostOwnership(post, loggedUser);

        post.setTitle(postAtualizado.getTitle());
        post.setImageUrl(postAtualizado.getImageUrl());
        post.setDescription(postAtualizado.getDescription());

        return postRepository.save(post);
    }


    // ========== DELETE ==========

    // deletar post
    public void deletePost(Long postId) {
        // busca o post
        Post post = globalHelperService.findPostById(postId);

        // pega o user logado
        User loggedUser = globalHelperService.getLoggedUser();

        // verifica se o user logado é o dono do post
        globalHelperService.validatePostOwnership(post, loggedUser);

        // deleta
        postRepository.delete(post);
    }


    private long seededOrderKey(Long postId, long seed) {
        long value = (postId == null) ? 0L : postId;
        long mixed = value ^ (seed * 0x9E3779B97F4A7C15L);
        mixed ^= (mixed >>> 33);
        mixed *= 0xff51afd7ed558ccdl;
        mixed ^= (mixed >>> 33);
        mixed *= 0xc4ceb9fe1a85ec53L;
        mixed ^= (mixed >>> 33);
        return mixed;
    }
}