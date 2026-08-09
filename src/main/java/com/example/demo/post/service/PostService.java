package com.example.demo.post.service;

import java.time.LocalDateTime;
import java.util.*;

import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.exeptions.tag.TagConflictException;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.post.dto.PostRequest;
import com.example.demo.post.dto.PostDetaisResponse;
import com.example.demo.entity.Tag;
import com.example.demo.post.dto.PostResponse;
import com.example.demo.post.mapper.PostMapper;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.repository.*;
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


    // ========== GET ==========

    // listar todos os posts
    public Page<PostDetaisResponse> getAllPosts(int page, int size, long seed) {
        User loggedUser = globalHelperService.getLoggedUser();

        Pageable pageable = PageRequest.of(page, size);
        List<Post> orderedPosts = new ArrayList<>(postRepository.findAll());

        orderedPosts.sort(Comparator
                .comparingLong((Post post) -> seededOrderKey(post.getId(), seed))
                .thenComparing(Post::getId));

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), orderedPosts.size());

        List<Post> pagedPosts = start >= orderedPosts.size()
                ? List.of()
                : orderedPosts.subList(start, end);

        return new PageImpl<>(pagedPosts, pageable, orderedPosts.size())
                .map(post -> postMapper.toPostDetaisResponse(post, loggedUser.getId()));
    }


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
                .findDistinctByContentContainingIgnoreCaseOrTagsNameContainingIgnoreCaseOrderByCreatedAtDesc(
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
                .findTop8DistinctByContentContainingIgnoreCaseOrTagsNameContainingIgnoreCaseOrderByCreatedAtDesc(
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