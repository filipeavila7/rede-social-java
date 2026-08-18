package com.example.demo.post.dto;

import com.example.demo.user.dto.UserResponse;
import com.example.demo.tag.entity.Tag;

import java.time.LocalDateTime;
import java.util.List;


// TODO - optar por usar esse dto
// pois no front não mostrará mais as curtidas no feed etc
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
