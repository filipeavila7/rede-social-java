package com.example.demo.profile.dto;

import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(max = 200, message = "No máximo 200 caracteres na bio")
        String bio,
        String imageUrlProfile,

        @Size(min = 2, max = 40, message = "Nome deve ter entre 2 e 40 caracteres")
        String name,

        @Size(min = 3, max = 20, message = "Username deve ter entre 3 e 20 caracteres")
        String userName,

        @Size(max = 30, message = "No máximo 30 caracteres na mensagem")
        String messageStatus
) {
}
