package com.example.demo.story.service;

import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.story.dto.StoryResponse;
import com.example.demo.story.mapper.StoryMapper;
import com.example.demo.story.repository.StoryRepository;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
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


    // mostrar todos os stories de um usuario pelo userName
    public Page<StoryResponse> getUserStories(String userName, Pageable pageable){
        // encontra o usuario
        User user = globalHelperService.findByUserName(userName);

        // verifica se o usuario logado segue o dono do story
        if (user.getProfile().isPrivateProfile()){
            globalHelperService.validateCanViewPrivateProfile(user.getId());
        }

        return storyRepository.findByUserUserNameAndExpiresAtAfterOrderByCreatedAtAsc(
                userName, LocalDateTime.now(), pageable)
                .map(storyMapper::toStoryResponse);
    }
}
