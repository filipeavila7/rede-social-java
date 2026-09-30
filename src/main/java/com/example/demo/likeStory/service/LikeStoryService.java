package com.example.demo.likeStory.service;

import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.likeStory.entity.LikeStory;
import com.example.demo.likeStory.repository.LikeStoryRepository;
import com.example.demo.story.entity.Story;
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
    private final GlobalHelperService globalHelperService;

    // curtir story
    public void likeStory(Long storyId){
        User loggedUser = globalHelperService.getLoggedUser();

        // valida o story
        Story story = globalHelperService.getStoryAndValidateAccess(storyId);

        LikeStory likeStory = new LikeStory();
        likeStory.setStory(story);
        likeStory.setUser(loggedUser);
        likeStory.setCreatedAt(LocalDateTime.now());

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
