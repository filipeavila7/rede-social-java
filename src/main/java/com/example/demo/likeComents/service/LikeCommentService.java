package com.example.demo.likeComents.service;

import com.example.demo.comment.entity.Comment;
import com.example.demo.exeptions.comment.CommentNotFoundException;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.likeComents.entity.LikeComment;
import com.example.demo.likeComents.repository.LikeCommentRepository;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeCommentService {
    private final LikeCommentRepository likeCommentRepository;
    private final GlobalHelperService globalHelperService;

    // curtir comentario
    public void likeComment(Long commentId){
        // pega o usuario
        User loggedUser = globalHelperService.getLoggedUser();

        // encontra o comentario
        Comment comment = globalHelperService.findByCommentId(commentId);

        // cria o like no comentario
        LikeComment likeComment = new LikeComment();
        likeComment.setUser(loggedUser);
        likeComment.setComment(comment);

        likeCommentRepository.save(likeComment);

    }

    public void deleteLikeComment(Long commentId){
        // pega o usuario
        User loggedUser = globalHelperService.getLoggedUser();

        // encontra o comentario
        LikeComment likeComment = likeCommentRepository.findByUserIdAndCommentId(loggedUser.getId(), commentId)
                .orElseThrow(CommentNotFoundException::new);

        // apaga
        likeCommentRepository.delete(likeComment);
    }

    // boelano para verificar se o usuario curtiu ou não (retorna no dto de comentario)
    public boolean likeCommentByMe(Long commentId){
        return likeCommentRepository
                .findByUserIdAndCommentId(globalHelperService.getLoggedUser().getId(), commentId)
                .isPresent();
    }


}
