package com.example.demo.feed.service;

import com.example.demo.feed.interest.entity.UserInterest;
import com.example.demo.feed.interest.repository.UserInterestRepository;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.post.dto.PostDetaisResponse;
import com.example.demo.post.entity.Post;
import com.example.demo.post.mapper.PostMapper;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.tag.entity.Tag;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private static final int CANDIDATE_POOL_SIZE = 500;
    private static final int WINDOW_DAYS = 30;

    private static final double INTEREST_WEIGHT = 0.7;
    private static final double POPULARITY_WEIGHT = 0.2;
    private static final double RECENCY_WEIGHT = 0.1;

    private static final double EXPLORATION_RATIO = 0.10; // 1 a cada 10 posts
    private static final double LOW_INTEREST_THRESHOLD = 0.05; // abaixo disso, conta como "não conhecido"

    private final PostRepository postRepository;
    private final UserInterestRepository userInterestRepository;
    private final PostMapper postMapper;
    private final GlobalHelperService globalHelperService;

    public Page<PostDetaisResponse> getFeed(int page, int size) {
        User loggedUser = globalHelperService.getLoggedUserOrNull();

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

        List<ScoredPost> scored = candidates.stream()
                .map(post -> {
                    double interestScore = normalizedInterestScore(post, tagScores, maxInterest);
                    double popularityScore = normalizedPopularity(post);
                    double recencyScore = normalizedRecency(post, now);

                    double finalScore =
                            INTEREST_WEIGHT * interestScore
                                    + POPULARITY_WEIGHT * popularityScore
                                    + RECENCY_WEIGHT * recencyScore;

                    return new ScoredPost(post, finalScore, interestScore);
                })
                .toList();

        return interleaveExploration(scored);
    }

    // separa "conhecido" de "exploração" e intercala na proporção definida
    private List<ScoredPost> interleaveExploration(List<ScoredPost> scored) {
        List<ScoredPost> known = scored.stream()
                .filter(sp -> sp.interestScore() > LOW_INTEREST_THRESHOLD)
                .sorted(Comparator.comparingDouble(ScoredPost::finalScore).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        Deque<ScoredPost> exploration = scored.stream()
                .filter(sp -> sp.interestScore() <= LOW_INTEREST_THRESHOLD)
                .sorted(Comparator.comparingDouble(ScoredPost::finalScore).reversed())
                .collect(Collectors.toCollection(ArrayDeque::new));

        if (exploration.isEmpty() || known.isEmpty()) {
            // nada pra explorar, ou nada conhecido ainda (usuário novo) — devolve como está
            List<ScoredPost> fallback = new ArrayList<>(known);
            fallback.addAll(exploration);
            return fallback;
        }

        int interval = (int) Math.round(1 / EXPLORATION_RATIO);
        List<ScoredPost> result = new ArrayList<>();

        for (int i = 0; i < known.size(); i++) {
            result.add(known.get(i));

            if ((i + 1) % interval == 0 && !exploration.isEmpty()) {
                result.add(exploration.poll());
            }
        }

        // se sobrou exploração sem "encaixe", entra no fim (better than descartar)
        result.addAll(exploration);

        return result;
    }

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

                    return new ScoredPost(post, finalScore, 0.0); // sem conceito de interesse pra anônimo
                })
                .sorted(Comparator.comparingDouble(ScoredPost::finalScore).reversed())
                .toList();
    }

    // ---------- fatores compartilhados ----------

    private double normalizedPopularity(Post post) {
        long likes = post.getLikesCount();
        long comments = post.getCommentsCount();
        double raw = likes + comments * 1.5;
        return Math.min(1.0, raw / 100.0);
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
                    .orElse(remaining.get(0));

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

    private record ScoredPost(Post post, double finalScore, double interestScore) {}
}