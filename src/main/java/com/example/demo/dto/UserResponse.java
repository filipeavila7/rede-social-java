package com.example.demo.dto;

public record UserResponse(
        Long id,
        String nome,
        String email,
        String profileImageUrl
) {
}
