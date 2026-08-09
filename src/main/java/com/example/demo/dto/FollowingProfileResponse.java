package com.example.demo.dto;

// dados do perfil dos usuários seguidos
public record FollowingProfileResponse(
        Long userId,
        String nome,
        String imageUrlProfile,
        String messageStatus,
        String userName
) {
}