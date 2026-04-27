package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.dto.PostResponse;
import com.example.demo.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;

@Service
public class PostService {
    // repository de user e post
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    // injetar no construtor
    public PostService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository,
            CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
    }

    // listar todos os posts
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::toPostResponse)
                .toList();
    }

    // criar um post usando o email do usuario logado
    public Post createPost(Post post) {
        // pega o email do usuário autenticado
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // adciona o email pego do user logado no find para encontrar ele no banco
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }
        // fk key
        // salvar usuario dono do post campo user_id fk key
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
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
                post.getLikes().size(),
                post.getComments().size()
        );
    }

}
