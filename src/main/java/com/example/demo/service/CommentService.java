package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Commente;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;

@Service
public class CommentService {
    public final CommentRepository commentRepository;
    public final UserRepository userRepository;
    public final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
            PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    // criar comentario em um post a partir do postId com user loogaado
    public Commente createCommente(Long postId, Commente novoComentario) {
        // pegar email do user logado
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        // encontrar o post pelo id passado
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        // fk key de user e post
        novoComentario.setPost(post);
        novoComentario.setUser(user);

        // salva no banco
        return commentRepository.save(novoComentario);

    }

    // listar todos os comentarios de um post passando o id dele
    public List<Commente> getAllPostCommentes(Long postId) {
        return commentRepository.findByPostId(postId);

    }

    // deletar um comentario do user logado passando o id do comentario
    public void deleteCommente(Long commentId) {
        Commente comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        // pegar email do user logado
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // caso o email do usuario do comentario e o email do logado seja diferente,
        // retorna erro
        if (!comment.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode apagar este comentário");
        }
        

        commentRepository.delete(comment);


    }
}