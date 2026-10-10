package com.example.demo.story.mapper;

import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.story.dto.*;
import com.example.demo.story.entity.Story;
import com.example.demo.story.entity.StoryType;
import com.example.demo.user.entity.User;
import com.example.demo.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StoryMapper {
    private final UserMapper userMapper;
    private final GlobalHelperService globalHelperService;


    public StoryResponse toStoryResponse(Story s) {
        return new StoryResponse(
                s.getId(),
                "/stories/" + s.getId() + "/image",
                s.getCreatedAt(),
                userMapper.toUserResponse(s.getUser()),
                s.getStoryType(),
                s.getVisibility(),
                s.getDescription(),
                globalHelperService.countVisibilitiesByStoryId(s.getId()),
                globalHelperService.isStoryLikedByMe(s.getId()),
                globalHelperService.isStoryViwed(s.getId()),
                s.getBackgroundColor() != null ? s.getBackgroundColor() : null
        );
    }

    public MyStorySummaryResponse toMyStorySummaryResponse(Story story) {
        return new MyStorySummaryResponse(
                story.getId(),
                "/stories/" + story.getId() + "/image",
                story.getCreatedAt(),
                story.getStoryType(),
                story.getDescription(),
                userMapper.toUserResponse(story.getUser()),
                globalHelperService.countVisibilitiesByStoryId(story.getId()),
                story.getVisibility(),
                story.getBackgroundColor() != null ? story.getBackgroundColor() : null
        );
    }

    public StorySummaryResponse toStorySummaryResponse(Story s){
        return new StorySummaryResponse(
                s.getId(),
                "/stories/" + s.getId() + "/image",
                s.getStoryType(),
                s.getDescription()
        );
    }

    public Story createStory(StoryRequest request){
        User loggedUser = globalHelperService.getLoggedUser();

        Story story = new Story();

        LocalDateTime now = LocalDateTime.now();

        story.setImageUrl(request.imageUrl());
        story.setCreatedAt(now);
        story.setExpiresAt(now.plusHours(24));
        story.setUser(loggedUser);
        story.setVisibility(request.visibility());
        story.setDescription(request.description());
        story.setStoryType(StoryType.IMAGE);

        return story;
    }

    public Story createTextStory(StoryTextRequest request){
        User loggedUser = globalHelperService.getLoggedUser();

        Story story = new Story();

        LocalDateTime now = LocalDateTime.now();

        story.setCreatedAt(now);
        story.setExpiresAt(now.plusHours(24));
        story.setUser(loggedUser);
        story.setVisibility(request.visibility());
        story.setDescription(request.text());
        story.setStoryType(StoryType.TEXT);
        story.setBackgroundColor(request.backgroundColor());

        return story;
    }
}
