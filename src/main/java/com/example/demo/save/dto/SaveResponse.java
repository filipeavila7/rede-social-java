package com.example.demo.save.dto;

import com.example.demo.post.dto.PostResponse;

import java.time.LocalDateTime;

public record SaveResponse(
        LocalDateTime createdAt,
        PostResponse postResponse
) {
}
