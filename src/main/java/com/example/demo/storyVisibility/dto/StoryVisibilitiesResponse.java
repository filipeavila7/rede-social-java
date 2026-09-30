package com.example.demo.storyVisibility.dto;


import com.example.demo.user.dto.UserResponse;

import java.time.LocalDateTime;

public record StoryVisibilitiesResponse(
        Long id,
        UserResponse user,
        LocalDateTime seenAt
) {
}
