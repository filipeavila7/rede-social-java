package com.example.demo.comment.service;

import java.util.List;

import com.example.demo.comment.dto.CommentRequest;
import com.example.demo.comment.mapper.CommentMapper;
import com.example.demo.dto.CommentResponse;
import com.example.demo.notification.dto.NotificationPostResponse;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.notification.service.NotificationService;
import com.example.demo.post.dto.PostSummaryResponse;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.service.WebSocketService;
import com.example.demo.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.comment.entity.Comment;
import com.example.demo.post.entity.Post;
import com.example.demo.user.entity.User;
import com.example.demo.comment.repository.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentService {
    public final CommentRepository commentRepository;
    public final WebSocketService webSocketService;
    public final GlobalHelperService  globalHelperService;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;



    // criar comentario em um post
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

        // salva no banco
        Comment saveComment = commentRepository.save(comment);

        // conteudo da notificação
        String content = loggedUser.getName() + " comentou no seu post: " + comment.getContent();

        // cria a notificação e ja envia via webSocket
        notificationService.createPostNotification(
                loggedUser, post.getUser(), post, NotificationType.COMMENT,
                content
        );


        return commentMapper.toCommentResponse(saveComment);

    }

    // listar todos os comentarios de um post passando o id dele
    public List<CommentResponse> getAllPostCommentes(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(this::toCommentResponse)
                .toList();

    }

    // deletar um comentario do user logado passando o id do comentario
    public void deleteCommente(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
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

    public CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                new UserResponse(
                        comment.getUser().getId(),
                        comment.getUser().getNome(),
                        comment.getUser().getProfile() != null
                                ? comment.getUser().getProfile().getImageUrlProfile()
                                : null,
                        comment.getUser().getUserName()
                ),
                new PostSummaryResponse(
                        comment.getPost().getId(),
                        comment.getPost().getContent(),
                        comment.getPost().getImageUrl()
                )
        );
    }
}