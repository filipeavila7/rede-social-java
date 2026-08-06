package com.example.demo.user.dto;

public record UserResponse(
        Long id,
        String nome,

        String profileImageUrl,
        String userName

) {
}
