package com.example.demo.dto;

import com.example.demo.entity.Tag;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String content,
        String imageUrl,
        UserResponse user,
        LocalDateTime createdAt,
        String description,
        List<Tag> tags,
        int likesCount,
        int commentsCount
) {
}
