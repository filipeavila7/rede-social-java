package com.example.demo.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String content,
        String imageUrl,
        UserResponse user,
        LocalDateTime createdAt,
        String description,
        int likesCount,
        int commentsCount
) {
}
