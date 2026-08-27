package com.example.demo.user.dto;

public record UserResponse(
        Long id,
        String name,

        String profileImageUrl,
        String userName

) {
}
