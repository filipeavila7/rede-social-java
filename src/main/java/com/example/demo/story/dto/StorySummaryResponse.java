package com.example.demo.story.dto;

import com.example.demo.story.entity.StoryType;

// criar o mapper e usar la no mapper de get de notificação
public record StorySummaryResponse(
        Long id,
        String imageUrl,
        StoryType storyType,
        String description
) {}
