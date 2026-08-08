package com.example.demo.comment.service;



import com.example.demo.comment.dto.CommentRequest;
import com.example.demo.comment.mapper.CommentMapper;
import com.example.demo.dto.CommentResponse;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.notification.service.NotificationService;
import com.example.demo.helpers.GlobalHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.comment.entity.Comment;
import com.example.demo.post.entity.Post;
import com.example.demo.user.entity.User;
import com.example.demo.comment.repository.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentService {
    public final CommentRepository commentRepository;
    public final GlobalHelperService  globalHelperService;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

    // TODO pro futuro, criar funcionalidae de responder comentarios em um post e curtir comentarios
    // TODO criar get que mostra todos os comentarios que o uusario ja fez permitindo gerenciar

    // ========== GET ==========

    // listar todos os comentarios de um post passando o id dele
    public Page<CommentResponse> getAllPostCommentes(Long postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                .map(commentMapper::toCommentResponse);

    }

    // ========== POST ==========

    // criar comentario em um post
    @Transactional
    public CommentResponse createCommente(Long postId, CommentRequest request) {
        // pegar user logado
        User loggedUser = globalHelperService.getLoggedUser();

        // encontrar o post
        Post post = globalHelperService.findPostById(postId);

        // cria o objeto de comentário
        Comment comment = new Comment();

        // cria o relacionamento
        comment.setPost(post);
        comment.setUser(loggedUser);

        comment.setContent(request.content());

        // conteudo da notificação
        String content = loggedUser.getName() + " comentou no seu post: " + comment.getContent();

        // cria a notificação e ja envia via webSocket
        notificationService.createPostNotification(
                loggedUser, post.getUser(), post, NotificationType.COMMENT,
                content
        );

        return commentMapper.toCommentResponse(commentRepository.save(comment));

    }

    // ========== DELETE ==========

    // deletar um comentario do user logado passando o id do comentario
    public void deleteCommente(Long commentId) {

        // verifica se o usuario é dono do comentario
        Comment comment = globalHelperService.validateCommentOwnership(
                commentId, globalHelperService.getLoggedUser());

        commentRepository.delete(comment);

    }

}