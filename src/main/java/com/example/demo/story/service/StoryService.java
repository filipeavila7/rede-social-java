package com.example.demo.story.service;


import com.example.demo.exeptions.api.AccessDeniedException;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.story.dto.MyStorySummaryResponse;
import com.example.demo.story.dto.StoryRequest;
import com.example.demo.story.dto.StoryResponse;
import com.example.demo.story.dto.StoryTextRequest;
import com.example.demo.story.entity.Story;
import com.example.demo.story.entity.StoryVisibility;
import com.example.demo.story.mapper.StoryMapper;
import com.example.demo.story.repository.StoryRepository;
import com.example.demo.upload.service.FileStorageService;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final GlobalHelperService globalHelperService;
    private final StoryRepository storyRepository;
    private final StoryMapper storyMapper;
    private final FileStorageService fileStorageService;


    // <img src={`/stories/${story.id}/image`} /> como o front mostrara imagens de story
    // TODO - criar entidade de stories de registro e adcionar qualquer novo story la
    // TODO - criar entidade e endpoint para vizualação de stories, salvando o usuario que viu e o id do story
    // mostrar todos os stories de um usuario pelo userName
    public Page<StoryResponse> getUserStories(String userName, Pageable pageable) {

        // encontra o dono dos stories
        User user = globalHelperService.findByUserName(userName);

        // usuário logado
        User loggedUser = globalHelperService.getLoggedUser();

        // verifica acesso ao perfil privado
        if (user.getProfile().isPrivateProfile()) {
            globalHelperService.validateCanViewPrivateProfile(user.getId());
        }

        // busca todos os stories ativos
        List<Story> stories = storyRepository
                .findByUserUserNameAndExpiresAtAfterOrderByCreatedAtAsc(
                        userName,
                        LocalDateTime.now()
                );

        // verifica uma única vez se o usuário logado é close friend ou se é o dono
        boolean isOwner = user.getId().equals(loggedUser.getId());

        boolean isCloseFriend = isOwner ||
                globalHelperService.isCloseFriends(
                        user.getId(),
                        loggedUser.getId()
                );

        List<StoryResponse> visibleStories = stories.stream()
                .filter(story ->
                        story.getVisibility() != StoryVisibility.CLOSE_FRIENDS
                                || isCloseFriend
                )
                .map(storyMapper::toStoryResponse)
                .toList();

        // aplica a paginação depois do filtro
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), visibleStories.size());


        List<StoryResponse> pageContent =
                start >= visibleStories.size()
                        ? List.of()
                        : visibleStories.subList(start, end);

        return new PageImpl<>(
                pageContent,
                pageable,
                visibleStories.size()
        );


    }


    // pegar stories do user logado
    public List<MyStorySummaryResponse> getMyStories() {

        User loggedUser = globalHelperService.getLoggedUser();

        List<Story> stories =
                storyRepository
                        .findByUserIdAndExpiresAtAfterOrderByCreatedAtAsc(
                                loggedUser.getId(),
                                LocalDateTime.now()
                        );

        return stories.stream()
                .map(storyMapper::toMyStorySummaryResponse)
                .toList();
    }


    // criar story de imagem
    public StoryResponse createStory(StoryRequest request){
        Story story = storyMapper.createStory(request);
        return storyMapper.toStoryResponse(storyRepository.save(story));

    }


    // criar story de texto
    public StoryResponse createTextStory(StoryTextRequest request){
        Story story = storyMapper.createTextStory(request);
        return  storyMapper.toStoryResponse(storyRepository.save(story));

    }


    // exluir story
    public void deleteStoryById(Long storyId){
        Story story =storyRepository.findByIdAndUserId(
                storyId, globalHelperService.getLoggedUser().getId())
                .orElseThrow(() -> new RuntimeException("Story not found"));

        storyRepository.delete(story);
    }


    public Resource getStoryImage(Long storyId) {

        User loggedUser = globalHelperService.getLoggedUser();

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));


        User user = story.getUser();

        // O dono sempre pode visualizar o próprio Story
        if (user.getId().equals(loggedUser.getId())) {
            return fileStorageService.loadPrivateStory(story.getImageUrl());
        }

        // Verifica se o Story ainda está disponível
        if (story.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AccessDeniedException();
        }

        // Se o perfil for privado, verifica se o usuário pode visualizar
        if (user.getProfile().isPrivateProfile()) {
            globalHelperService.validateCanViewPrivateProfile(user.getId());
        }

        // Se o Story for somente para Melhores Amigos
        if (story.getVisibility() == StoryVisibility.CLOSE_FRIENDS) {

            boolean isCloseFriend = globalHelperService.isCloseFriends(
                            user.getId(),
                            loggedUser.getId()
            );

            if (!isCloseFriend) {
                throw new AccessDeniedException();
            }
        }



        return fileStorageService.loadPrivateStory(story.getImageUrl());
    }
}
