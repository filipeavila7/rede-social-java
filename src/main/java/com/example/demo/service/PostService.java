package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.*;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.Tag;
import com.example.demo.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       LikeRepository likeRepository,
                       CommentRepository commentRepository,
                       TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.tagRepository = tagRepository;
    }

    private User getLoggedUser() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        return user;
    }

    // listar todos os posts
    public Page<PostResponse> getAllPosts(int page, int size, long seed) {
        User loggedUser = getLoggedUser();

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
                .map(post -> toPostResponse(post, loggedUser.getId()));
    }

    // criar post
    public Post createPost(PostDto dto) {
        User user = getLoggedUser();

        if (dto.tagIds().size() > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Máximo de 3 tags");
        }

        Set<Long> uniqueTags = new HashSet<>(dto.tagIds());

        if (uniqueTags.size() != dto.tagIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tags duplicadas não são permitidas");
        }

        List<Tag> tags = tagRepository.findAllById(dto.tagIds());

        if (tags.size() != dto.tagIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uma ou mais tags são inválidas");
        }

        Post post = new Post();
        post.setContent(dto.content());
        post.setDescription(dto.description());
        post.setImageUrl(dto.imageUrl());
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(user);
        post.setTags(tags);

        return postRepository.save(post);
    }

    // buscar post pelo id
    public PostResponse getPostById(Long id) {
        User loggedUser = getLoggedUser();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        return toPostResponse(post, loggedUser.getId());
    }

    // editar post
    public Post updatePost(Long id, Post postAtualizado) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        User loggedUser = getLoggedUser();

        if (!post.getUser().getEmail().equals(loggedUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode editar este post");
        }

        post.setContent(postAtualizado.getContent());
        post.setImageUrl(postAtualizado.getImageUrl());
        post.setDescription(postAtualizado.getDescription());

        return postRepository.save(post);
    }

    // deletar post
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        User loggedUser = getLoggedUser();

        if (!post.getUser().getEmail().equals(loggedUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode apagar este post");
        }

        postRepository.deleteById(id);
    }

    // posts do usuario logado
    public Page<PostResponse> getMyPosts(int page, int size) {
        User user = getLoggedUser();

        Pageable pageable = PageRequest.of(page, size);

        return postRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(post -> toPostResponse(post, user.getId()));
    }

    // posts de outro usuario
    public Page<PostResponse> getPostsByUserName(String userName, int page, int size) {
        User loggedUser = getLoggedUser();

        Pageable pageable = PageRequest.of(page, size);

        return postRepository
                .findByUserUserNameOrderByCreatedAtDesc(userName, pageable)
                .map(post -> toPostResponse(post, loggedUser.getId()));
    }

    // quantidade de posts
    public long getPostsCountByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }

    // stats isoladas
    public Map<String, Long> getPostStats(Long postId) {
        long likes = likeRepository.countByPostId(postId);
        long comments = commentRepository.countByPostId(postId);

        Map<String, Long> stats = new HashMap<>();
        stats.put("likes", likes);
        stats.put("comments", comments);
        return stats;
    }

    // conversor para dto
    public PostResponse toPostResponse(Post post, Long loggedUserId) {

        long likesCount = likeRepository.countByPostId(post.getId());
        long commentsCount = commentRepository.countByPostId(post.getId());
        boolean likedByMe = likeRepository.existsByUserIdAndPostId(loggedUserId, post.getId());

        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                new UserResponse(
                        post.getUser().getId(),
                        post.getUser().getNome(),
                        post.getUser().getProfile().getImageUrlProfile(),
                        post.getUser().getUserName()
                ),
                post.getCreatedAt(),
                post.getDescription(),
                post.getTags(),
                likesCount,
                commentsCount,
                likedByMe
        );
    }

    // pesquisar posts pelo titulo (content)
    public Page<PostResponse> searchPosts(String termo, int page, int size) {

        if (termo == null || termo.trim().isEmpty()) {
            return Page.empty();
        }

        User loggedUser = getLoggedUser();

        Pageable pageable = PageRequest.of(page, size);

        return postRepository
                .findDistinctByContentContainingIgnoreCaseOrTagsNameContainingIgnoreCaseOrderByCreatedAtDesc(
                        termo.trim(),
                        termo.trim(),
                        pageable
                )
                .map(post -> toPostResponse(post, loggedUser.getId()));
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

                    if (post.getContent() != null) {
                        resultados.add(post.getContent());
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