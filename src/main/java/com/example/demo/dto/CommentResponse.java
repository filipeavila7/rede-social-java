package com.example.demo.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        UserResponse user,
        PostSummaryResponse post

) {}
