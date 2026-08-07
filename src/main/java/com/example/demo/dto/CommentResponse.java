package com.example.demo.dto;

import com.example.demo.post.dto.PostSummaryResponse;
import com.example.demo.user.dto.UserResponse;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        UserResponse user,
        PostSummaryResponse post

) {}
