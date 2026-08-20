package com.example.demo.comment.service;



import com.example.demo.comment.dto.CommentRequest;
import com.example.demo.comment.mapper.CommentMapper;
import com.example.demo.comment.dto.CommentResponse;
import com.example.demo.exeptions.comment.CommentNotFoundException;
import com.example.demo.feed.interest.service.UserInterestService;
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
    private final UserInterestService userInterestService;

    // TODO pro futuro, criar funcionalidae de responder comentarios em um post e curtir comentarios
    // TODO criar get que mostra todos os comentarios que o usuario ja fez permitindo gerenciar

    // ========== GET ==========

    // listar todos os comentarios de um post passando o id dele
    public Page<CommentResponse> getAllPostCommentes(Long postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                .map(commentMapper::toCommentResponse);

    }

    // ver resposta dos comentarios
    public Page<CommentResponse> getCommentReplys(Long commentId, Pageable pageable){
        // verifica se o comentario existe
        globalHelperService.findByCommentId(commentId);
        // retorna as resosta desse comentario
        return commentRepository.findByParentCommentId(commentId, pageable)
                .map(commentMapper::toCommentResponse);
    }


    // boleano se existe resposta no comentario ou não (usar no dto de comentario)
    public boolean exixstReplys(Long commentId){
        return commentRepository.existsByParentCommentId(commentId);
    }


    // contar quantas respostas um comentario tem
    public long countReplys(Long commentId){
        return commentRepository.countByParentCommentId(commentId);
    }

    // ========== POST ==========

    // criar comentario em um post
    @Transactional
    public CommentResponse createCommente(Long postId, CommentRequest request) {
        User loggedUser = globalHelperService.getLoggedUser();
        Post post = globalHelperService.findPostById(postId);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(loggedUser);
        comment.setContent(request.content());
        comment.setParentComment(null);

        Comment saved = commentRepository.save(comment);

        // atualiza o perfil de interesse com o peso do comentário
        userInterestService.applyDelta(loggedUser, post, UserInterestService.commentWeight());

        String content = loggedUser.getName() + " comentou no seu post: " + comment.getContent();
        notificationService.createPostNotification(
                loggedUser, post.getUser(), post, NotificationType.COMMENT, content);

        return commentMapper.toCommentResponse(saved);
    }


    // responder um comentario
    public CommentResponse replyComment(Long commentId, Long postId, CommentRequest request){
        // acha usuario
        User loggedUser = globalHelperService.getLoggedUser();

        // acha post
        Post post = globalHelperService.findPostById(postId);

        // acha o comentario que sera respondido
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(CommentNotFoundException::new);

        // cria a resposta
        Comment replyComment = new Comment();

        replyComment.setPost(post);
        replyComment.setUser(loggedUser);
        replyComment.setContent(request.content());
        replyComment.setParentComment(comment); // coloca o comentario que foi respondido


        Comment save = commentRepository.save(replyComment);

        userInterestService.applyDelta(loggedUser, post, UserInterestService.commentWeight());

        String content = loggedUser.getName() + " respondeu o seu comentario: " + request.content();

        // gerar notificação para respostas de comentarios
        notificationService.createCommentNotification(
                loggedUser, comment.getUser(), post, comment, NotificationType.REPLY, content);

        return commentMapper.toCommentResponse(save);
    }

    // ========== DELETE ==========

    // deletar um comentario do user logado passando o id do comentario
    @Transactional
    public void deleteCommente(Long commentId) {
        User loggedUser = globalHelperService.getLoggedUser();

        Comment comment = globalHelperService.validateCommentOwnership(commentId, loggedUser);
        Post post = comment.getPost();

        commentRepository.delete(comment);

        // reverte o peso do comentário no perfil de interesse
        userInterestService.applyDelta(loggedUser, post, -UserInterestService.commentWeight());
    }

}