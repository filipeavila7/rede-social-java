package com.example.demo.storyVisibility.mapper;

import com.example.demo.storyVisibility.dto.StoryVisibilitiesResponse;
import com.example.demo.storyVisibility.entity.StoryVisibilities;
import com.example.demo.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoryVisibilitiesMapper {
    private final UserMapper userMapper;

    public StoryVisibilitiesResponse toStoryVisibilitiesResponse(StoryVisibilities s){
        return new StoryVisibilitiesResponse(
                s.getId(),
                userMapper.toUserResponse(s.getUser()),
                s.getCreatedAt()
        );
    }
}
