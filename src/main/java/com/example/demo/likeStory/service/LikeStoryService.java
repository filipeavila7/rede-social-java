package com.example.demo.likeStory.service;

import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.likeStory.entity.LikeStory;
import com.example.demo.likeStory.repository.LikeStoryRepository;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.notification.service.NotificationService;
import com.example.demo.story.entity.Story;
import com.example.demo.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeStoryService {
    private final LikeStoryRepository likeStoryRepository;
    private final GlobalHelperService globalHelperService;
    private final NotificationService notificationService;

    // curtir story
    @Transactional
    public void likeStory(Long storyId){
        User loggedUser = globalHelperService.getLoggedUser();

        // valida o story
        Story story = globalHelperService.getStoryAndValidateAccess(storyId);

        LikeStory likeStory = new LikeStory();
        likeStory.setStory(story);
        likeStory.setUser(loggedUser);
        likeStory.setCreatedAt(LocalDateTime.now());

        // cria notificação de like
        notificationService.createStoryNotification(
                loggedUser, story.getUser(), story, NotificationType.LIKE, " curtiu o seu story"
        );

        likeStoryRepository.save(likeStory);
    }


    // remover curtida
    public void unlikeStory(Long storyId) {

        User loggedUser = globalHelperService.getLoggedUser();

        LikeStory likeStory = likeStoryRepository
                .findByStoryIdAndUserId(
                        storyId,
                        loggedUser.getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curtida não encontrada"
                ));

        likeStoryRepository.delete(likeStory);
    }
}
