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
        return postRepository.findByUserId(user.getId());

    }

    // buscar posts de outros usuarios pelo email
    public List<Post> getPostsByUserEmail(String email) {
        return postRepository.findByUserEmail(email);
    }

}
