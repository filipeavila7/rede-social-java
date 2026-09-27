package com.example.demo.story.dto;

import com.example.demo.user.dto.UserResponse;


import java.time.LocalDateTime;

public record StoryResponse(
        Long id,
        String imageUrl,
        LocalDateTime createdAt,
        UserResponse OwerUser
) {
}
