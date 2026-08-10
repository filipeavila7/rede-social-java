
package com.example.demo.profile.dto;

// dados do perfil do usuario logado
public record ProfileResponse(
        Long id,
        String nome,
        String bio,
        String imageUrlProfile,
        String messageStatus,
        String userName
) {}
