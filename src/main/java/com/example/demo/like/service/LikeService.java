package com.example.demo.like.service;

import com.example.demo.feed.interest.service.UserInterestService;
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
    private final UserInterestService userInterestService;

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
        User loggedUser = globalHelperService.getLoggedUser();
        Post post = globalHelperService.findPostById(postId);

        globalHelperService.verifyLikeInPost(loggedUser.getId(), postId);

        Like like = new Like();
        like.setPost(post);
        like.setUser(loggedUser);

        LikeResponse response = likeMapper.toLikeResponse(likeRepository.save(like));

        // atualiza o perfil de interesse com o peso do like
        userInterestService.applyDelta(loggedUser, post, UserInterestService.likeWeight());

        String content = loggedUser.getName() + " curtiu o seu post";
        notificationService.createPostNotification(
                loggedUser, post.getUser(), post, NotificationType.LIKE, content);

        return response;
    }

    // remover uma curtida
    @Transactional
    public void unlikePost(Long postId) {
        User loggedUser = globalHelperService.getLoggedUser();

        Like like = globalHelperService.findLikeByUserIdAndPostId(loggedUser.getId(), postId);
        Post post = like.getPost();

        likeRepository.delete(like);

        // reverte o peso do like no perfil de interesse
        userInterestService.applyDelta(loggedUser, post, -UserInterestService.likeWeight());
    }





}