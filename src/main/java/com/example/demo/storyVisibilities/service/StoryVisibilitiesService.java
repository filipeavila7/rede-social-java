package com.example.demo.storyVisibilities.service;


import com.example.demo.exeptions.api.AccessDeniedException;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.story.entity.Story;
import com.example.demo.story.repository.StoryRepository;
import com.example.demo.storyVisibilities.dto.StoryVisibilitiesResponse;
import com.example.demo.storyVisibilities.entity.StoryVisibilities;
import com.example.demo.storyVisibilities.mapper.StoryVisibilitiesMapper;
import com.example.demo.storyVisibilities.repository.StoryVisibilitiesRepository;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StoryVisibilitiesService {
    private final StoryVisibilitiesRepository storyVisibilityRepository;
    private final GlobalHelperService globalHelperService;
    private final StoryVisibilitiesMapper storyVisibilitiesMapper;
    private final StoryRepository storyRepository;


    // retornar todas as vizualizações de um story, so o user dono pode ver
    public Page<StoryVisibilitiesResponse> getStoryVisibilities(
            Long storyId,
            Pageable pageable
    ) {
        // acha o story
        Story story = storyRepository.findByIdAndExpiresAtAfter(
                storyId,
                LocalDateTime.now()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Story não encontrado"
        ));

        User loggedUser = globalHelperService.getLoggedUser();

        // se o dono for diferente do logado
        if (!story.getUser().getId().equals(loggedUser.getId())) {
            throw new AccessDeniedException();
        }

        return storyVisibilityRepository.findByStoryId(storyId, pageable)
                .map(storyVisibilitiesMapper::toStoryVisibilitiesResponse);
    }

    // criar visualização
    public void createStoryVisibility(Long storyId){
        User loggedUser = globalHelperService.getLoggedUser();
        Story story = globalHelperService.getStoryAndValidateAccess(storyId);

        // não registra visualização do próprio story
        if (!story.getUser().getId().equals(loggedUser.getId())) {

            boolean alreadyViewed =
                    storyVisibilityRepository.existsByStoryIdAndUserId(
                            storyId,
                            loggedUser.getId()
                    );

            if (!alreadyViewed) {
                StoryVisibilities storyVisibility = new StoryVisibilities();

                storyVisibility.setStory(story);
                storyVisibility.setUser(loggedUser);
                storyVisibility.setCreatedAt(LocalDateTime.now());

                storyVisibilityRepository.save(storyVisibility);
            }
        }
    }
}
