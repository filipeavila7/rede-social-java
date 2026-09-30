package com.example.demo.storyVisibilities.dto;


import com.example.demo.user.dto.UserResponse;

import java.time.LocalDateTime;

public record StoryVisibilitiesResponse(
        Long id,
        UserResponse user,
        LocalDateTime seenAt
) {
}
