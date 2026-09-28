package com.example.demo.story.service;


import com.example.demo.exeptions.api.AccessDeniedException;
import com.example.demo.helpers.GlobalHelperService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final GlobalHelperService globalHelperService;
    private final StoryRepository storyRepository;
    private final StoryMapper storyMapper;
    private final FileStorageService fileStorageService;


    // <img src={`/stories/${story.id}/image`} /> como o front mostrara imagens de story
    // TODO - criar lista de melhores amigos e enum de tipo de visibilidade de story
    // TODO - se caso a visibilidade seja close, porcura se existe um relacionamento entre o logado e o dono
    // TODO - criar entidade e endpoint para vizualação de stories, salvando o usuario que viu e o id do story
    // mostrar todos os stories de um usuario pelo userName
    public Page<StoryResponse> getUserStories(String userName, Pageable pageable){
        // encontra o usuario
        User user = globalHelperService.findByUserName(userName);

        // verifica se o usuario logado segue o dono do story
        if (user.getProfile().isPrivateProfile()){
            globalHelperService.validateCanViewPrivateProfile(user.getId());
        }

        // retorna todos os stories validos
        return storyRepository.findByUserUserNameAndExpiresAtAfterOrderByCreatedAtAsc(
                        userName, LocalDateTime.now(), pageable)
                .map(storyMapper::toStoryResponse);
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


    public Resource getStoryImage(Long storyId) {

        User loggedUser = globalHelperService.getLoggedUser();

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        User user = story.getUser();

        // O dono sempre pode visualizar o próprio Story
        if (user.getId().equals(loggedUser.getId())) {
            return fileStorageService.loadPrivateStory(story.getImageUrl());
        }

        // Se o perfil for privado, verifica se o usuário pode visualizar
        if (user.getProfile().isPrivateProfile()) {
            globalHelperService.validateCanViewPrivateProfile(user.getId());
        }

        // Se o Story for somente para Melhores Amigos
        if (story.getVisibility() == StoryVisibility.CLOSE_FRIENDS) {

            boolean isCloseFriend =
                    closeFriendRepository.existsByUserIdAndFriendId(
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
