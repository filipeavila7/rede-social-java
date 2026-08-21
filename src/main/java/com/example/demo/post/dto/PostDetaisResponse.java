package com.example.demo.post.dto;

import com.example.demo.user.dto.UserResponse;
import com.example.demo.tag.entity.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record PostDetaisResponse(
        Long id,
        String title,
        String imageUrl,
        UserResponse user,
        LocalDateTime createdAt,
        String description,
        Set<Tag> tags,
        long likesCount,
        long commentsCount,
        boolean likedByMe
) {
}
