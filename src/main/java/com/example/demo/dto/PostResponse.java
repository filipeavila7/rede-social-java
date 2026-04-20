package com.example.demo.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String content,
        String imageUrl,
        UserResponse user,
        LocalDateTime createdAt,
        int likesCount,
        int commentsCount
) {
}
