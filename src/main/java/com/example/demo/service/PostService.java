package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;

@Service
public class PostService {
    // repository de user e post
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // injetar no construtor
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // listar todos os posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // criar um post
    public Post createPost(Post post) {
        // pega o email do usuário autenticado
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }
        // fk key
        post.setUser(user);
        return postRepository.save(post);
    }

    // buscar post pelo id
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));
    }

    // editar um post
    public Post updatePost(Long id, Post postAtualizado) {
        // busca o post pelo o id passado
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        // substitui os valores
        post.setContent(postAtualizado.getContent());
        post.setImageUrl(postAtualizado.getImageUrl());

        // salvar alterações
        return postRepository.save(post);

    }

    // deletar um post
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado");
        }

        postRepository.deleteById(id);
    }

    // buscar posts do usuario logado
    public List<Post> getMyPosts() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email);
        return postRepository.findByUserId(user.getId());

    }

    // buscar posts de outros usuarios pelo email
    public List<Post> getPostsByUserEmail(String email) {
        return postRepository.findByUserEmail(email);
    }

}
