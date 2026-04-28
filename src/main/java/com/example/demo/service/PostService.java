package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // repository de user e post
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;

    // injetar no construtor


    public PostService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository, CommentRepository commentRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.tagRepository = tagRepository;
    }

    // listar todos os posts
    public Page<PostResponse> getAllPosts(int page, int size, long seed) {
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
                .map(this::toPostResponse);
    }

    // criar um post usando o email do usuario logado
    public Post createPost(PostDto dto) {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        List<Tag> tags = tagRepository.findAllById(dto.tagIds());

        if (tags.size() > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Máximo de 3 tags");
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
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        return toPostResponse(post);
    }

    // editar um post
    public Post updatePost(Long id, Post postAtualizado) {
        // busca o post pelo o id passado
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        // pegar email do usuario logado
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // verifica se ele é o dono daquele post
        if (!post.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode editar este post");
        }

        // substitui os valores
        post.setContent(postAtualizado.getContent());
        post.setImageUrl(postAtualizado.getImageUrl());
        post.setDescription(postAtualizado.getDescription());

        // salvar alterações
        return postRepository.save(post);

    }

    // deletar um post
    public void deletePost(Long id) {
        // referencia do post encontrado pelo id passado na url da requisição
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        // pegar email do usario logado no momento, para garantir que ele so apague os
        // posts dele
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // se email daquele usuario logado não pertencer ao usuario dono do post, ele
        // não deixa apagar
        if (!post.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode apagar este post");
        }

        // caso o email seja o mesmo ele apaga
        postRepository.deleteById(id);
    }

    // buscar posts do usuario logado
    public List<Post> getMyPosts() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email);
        return postRepository.findByUserIdOrderByCreatedAtDescIdDesc(user.getId());

    }

    // buscar posts de outros usuarios pelo email
    public List<Post> getPostsByUserEmail(String email) {
        return postRepository.findByUserEmailOrderByCreatedAtDescIdDesc(email);
    }

    // contar total de posts de um usuario pelo id
    public long getPostsCountByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }

    // calcular total de curtidas de um post pelo seu id
    public Map<String, Long> getPostStats(Long postId) {
        // likes
        long likes = likeRepository.countByPostId(postId);
        // comentarios
        long comments = commentRepository.countByPostId(postId);

        Map<String, Long> stats = new HashMap<>();
        stats.put("likes", likes);
        stats.put("comments", comments);
        return stats;
    }

    public PostResponse toPostResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                new UserResponse(
                        post.getUser().getId(),
                        post.getUser().getNome(),
                        post.getUser().getEmail(),
                        post.getUser().getProfile().getImageUrlProfile()
                ),
                post.getCreatedAt(),
                post.getDescription(),
                post.getTags(),
                post.getLikes().size(),
                post.getComments().size()
        );
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
