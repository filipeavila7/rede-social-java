package com.example.demo.closeFriends.dto;

import com.example.demo.user.dto.UserResponse;

import java.time.LocalDateTime;

public record CloseFriendsResponse(
        Long id,
        UserResponse friend,
        LocalDateTime createdAt
) {
}
