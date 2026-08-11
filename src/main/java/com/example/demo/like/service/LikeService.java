package com.example.demo.like.service;

import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.like.dto.LikeResponse;
import com.example.demo.like.mapper.LikeMapper;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.demo.like.entity.Like;
import com.example.demo.post.entity.Post;
import com.example.demo.user.entity.User;
import com.example.demo.like.repository.LikeRepository;

@Service
@RequiredArgsConstructor
public class LikeService {
    public final LikeRepository likeRepository;
    public final NotificationService notificationService;
    private final GlobalHelperService globalHelperService;
    private final LikeMapper likeMapper;


    // ========== GET ==========

    // true ou false, se existe curtida ou não
    // TODO - POSSÍVEL METODO REDUNDANTE
    public boolean hasUserLikedPost(Long postId) {
        // verifica se existe like
        return globalHelperService.existsLikeInPost(
                globalHelperService.getLoggedUser().getId(), postId
        );
    }

    // ========== POST ==========

    // curtir post pelo id do post com o user logado
    @Transactional
    public LikeResponse createLike(Long postId) {
        // pega o user logado
        User loggedUser = globalHelperService.getLoggedUser();

        // encontra o post
        Post post = globalHelperService.findPostById(postId);

        // verifica se existe curtida
        globalHelperService.verifyLikeInPost(loggedUser.getId(), postId);

        // cria o like
        Like like = new Like();
        like.setPost(post);
        like.setUser(loggedUser);

        // content da notificação
        String content = loggedUser.getName() + " curtiu o seu post";

        // cria a notificação
        notificationService.createPostNotification(
                loggedUser, post.getUser(), post, NotificationType.LIKE, content);

        return likeMapper.toLikeResponse(likeRepository.save(like));
    }


    // ========== DELETE ==========

    // remover uma curtida
    @Transactional
    public void unlikePost(Long postId) {
        // pegar curtida existente pelo usuario logado no post curtido
        Like like = globalHelperService.findLikeByUserIdAndPostId(
                globalHelperService.getLoggedUser().getId(), postId);

        likeRepository.delete(like);
    }



}