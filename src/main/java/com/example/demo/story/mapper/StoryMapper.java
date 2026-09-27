package com.example.demo.story.mapper;

import com.example.demo.story.dto.StoryResponse;
import com.example.demo.story.entity.Story;
import com.example.demo.user.mapper.UserMapper;
import com.example.demo.util.FileUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoryMapper {
    private final FileUrlUtils fileUrlUtils;
    private final UserMapper userMapper;

    public StoryResponse toStoryResponse(Story s){
        return new StoryResponse(
                s.getId(),
                fileUrlUtils.toPublicUrl(s.getImageUrl()),
                s.getCreatedAt(),
               userMapper.toUserResponse(s.getUser())
        );
    }
}
