package com.example.demo.story.dto;

import com.example.demo.story.entity.StoryType;
import com.example.demo.story.entity.StoryVisibility;
import com.example.demo.user.dto.UserResponse;


import java.time.LocalDateTime;

public record StoryResponse(
        Long id,
        String imageUrl,
        LocalDateTime createdAt,
        UserResponse OwerUser,
        StoryType storyType,
        StoryVisibility storyVisibility,
        long totalVisibilities,
        boolean isLikedByMe,
        boolean viewed
) {
}
