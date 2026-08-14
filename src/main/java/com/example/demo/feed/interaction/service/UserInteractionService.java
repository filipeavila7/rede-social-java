package com.example.demo.feed.interaction.service;

import com.example.demo.feed.interaction.entity.InteractionType;
import com.example.demo.feed.interaction.entity.UserInteraction;
import com.example.demo.feed.interaction.repository.UserInteractionRepository;
import com.example.demo.feed.interest.service.UserInterestService;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.post.entity.Post;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInteractionService {

    private final UserInteractionRepository userInteractionRepository;
    private final UserInterestService userInterestService;
    private final GlobalHelperService globalHelperService;

    @Transactional
    public void registerView(Long postId, int durationSeconds) {
        if (durationSeconds < 10) {
            return;
        }
        if (durationSeconds > 300) {
            durationSeconds = 300;
        }

        User loggedUser = globalHelperService.getLoggedUser();
        Post post = globalHelperService.findPostById(postId);

        saveInteraction(loggedUser, post, InteractionType.VIEW, durationSeconds);
        userInterestService.registerInterest(loggedUser, post, InteractionType.VIEW, durationSeconds);
    }

    @Transactional
    public void registerSave(Long postId) {
        User loggedUser = globalHelperService.getLoggedUser();
        Post post = globalHelperService.findPostById(postId);

        saveInteraction(loggedUser, post, InteractionType.SAVE, null);
        userInterestService.registerInterest(loggedUser, post, InteractionType.SAVE, null);
    }

    @Transactional
    public void registerShare(Long postId) {
        User loggedUser = globalHelperService.getLoggedUser();
        Post post = globalHelperService.findPostById(postId);

        saveInteraction(loggedUser, post, InteractionType.SHARE, null);
        userInterestService.registerInterest(loggedUser, post, InteractionType.SHARE, null);
    }

    private void saveInteraction(User user, Post post, InteractionType type, Integer durationSeconds) {
        UserInteraction interaction = new UserInteraction();
        interaction.setUser(user);
        interaction.setPost(post);
        interaction.setType(type);
        interaction.setDurationSeconds(durationSeconds);

        userInteractionRepository.save(interaction);
    }

    // TODO - futuras formas de interação
}