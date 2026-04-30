package com.example.demo.dto;

public record UserResponse(
        Long id,
        String nome,

        String profileImageUrl,
        String userName

) {
}
