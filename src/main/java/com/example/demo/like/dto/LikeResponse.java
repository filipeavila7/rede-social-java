package com.example.demo.like.dto;

import com.example.demo.post.dto.PostResponse;


import java.time.LocalDateTime;

public record LikeResponse(
        PostResponse post,
        LocalDateTime createdAt
) {
}
