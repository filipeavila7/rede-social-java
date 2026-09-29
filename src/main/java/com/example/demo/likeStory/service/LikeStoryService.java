package com.example.demo.likeStory.service;

import com.example.demo.exeptions.api.AccessDeniedException;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.likeStory.entity.LikeStory;
import com.example.demo.likeStory.repository.LikeStoryRepository;
import com.example.demo.story.entity.Story;
import com.example.demo.story.entity.StoryVisibility;
import com.example.demo.story.repository.StoryRepository;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeStoryService {
    private final LikeStoryRepository likeStoryRepository;
    private final StoryRepository storyRepository;
    private final GlobalHelperService globalHelperService;

    // curtir story
    public void likeStory(Long storyId){
        User loggedUser = globalHelperService.getLoggedUser();

        // acha o story
        Story story = storyRepository.findByIdAndExpiresAtAfter(
                storyId,
                LocalDateTime.now()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Story não encontrado"
        ));

        // verifica se o usuario segue o dono do story
        if (story.getUser().getProfile().isPrivateProfile()) {
            globalHelperService.validateCanViewPrivateProfile(story.getUser().getId());
        }

        // caso o story seja de melhores amigos, verifica se o usuario esta nele
        if (story.getVisibility() == StoryVisibility.CLOSE_FRIENDS) {

            boolean isCloseFriend = globalHelperService.isCloseFriends(
                    story.getUser().getId(),
                    loggedUser.getId()
            );

            if (!isCloseFriend) {
                throw new AccessDeniedException();
            }
        }

        LikeStory likeStory = new LikeStory();
        likeStory.setStory(story);
        likeStory.setUser(loggedUser);
        likeStory.setCreatedAt(LocalDateTime.now());

        likeStoryRepository.save(likeStory);
    }
}
