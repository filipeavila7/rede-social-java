package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.dto.CommentResponse;
import com.example.demo.dto.NotificationRealtimeResponse;
import com.example.demo.dto.PostSummaryResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.Notification;
import com.example.demo.repository.NotificationRepository;
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
    public final WebSocketService webSocketService;
    public final NotificationRepository notificationRepository;


    public CommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository, WebSocketService webSocketService, NotificationRepository notificationRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.webSocketService = webSocketService;
        this.notificationRepository = notificationRepository;
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

        Commente saveComment = commentRepository.save(novoComentario);

        // só notifica se não for o próprio post
        if (!post.getUser().getId().equals(user.getId())) {

            // salva a notificação no banco
            Notification notification = new Notification();
            notification.setType("COMMENT");
            notification.setContent(user.getNome() + " comentou: " + novoComentario.getContent());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setIsRead(false);
            notification.setSender(user);
            notification.setReceiver(post.getUser());
            notification.setPost(post);

            notificationRepository.save(notification);

            // cria a notificação para enviar via webSocket
            NotificationRealtimeResponse dto =
                    new NotificationRealtimeResponse(
                            "COMMENT",
                            user.getId(),
                            user.getNome(),
                            user.getProfile() != null
                                    ? user.getProfile().getImageUrlProfile()
                                    : null,
                            postId,
                            null,
                            null,
                            notification.getContent(),
                            LocalDateTime.now()
                    );

            // enviar notificação
            webSocketService.sendNotificationToUser(
                    post.getUser().getId(),
                    dto
            );
        }

        // salva no banco
        return saveComment;

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

    public CommentResponse toCommentResponse(Commente comment) {
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