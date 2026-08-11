package com.example.demo.post.dto;

import com.example.demo.user.dto.UserResponse;
import com.example.demo.tag.entity.Tag;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String content,
        String imageUrl,
        UserResponse user,
        LocalDateTime createdAt,
        String description,
        List<Tag> tags
) {
}
