package com.example.demo.story.dto;

import com.example.demo.story.entity.StoryType;
import com.example.demo.story.entity.StoryVisibility;
import com.example.demo.user.dto.UserResponse;

import java.time.LocalDateTime;

public record MyStorySummaryResponse(
        Long id,
        String imageUrl,
        LocalDateTime createdAt,
        StoryType storyType,
        String description,
        UserResponse ownerUser,
        long totalVisibilities,
        StoryVisibility storyVisibility
) {}