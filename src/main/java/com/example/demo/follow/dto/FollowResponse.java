package com.example.demo.follow.dto;

import com.example.demo.user.dto.UserResponse;

import java.time.LocalDateTime;

public record FollowResponse(
        UserResponse followed,
        LocalDateTime createAt
) {
}

