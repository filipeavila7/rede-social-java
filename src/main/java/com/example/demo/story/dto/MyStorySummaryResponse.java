package com.example.demo.story.dto;

import com.example.demo.story.entity.StoryType;

import java.time.LocalDateTime;

public record MyStorySummaryResponse(
        Long id,
        String imageUrl,
        LocalDateTime createdAt,
        StoryType storyType,
        String description,
        long totalVisibilities
) {}