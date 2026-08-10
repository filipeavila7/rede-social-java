package com.example.demo.profile.dto;

import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(max = 200, message = "No máximo 200 caracteres na bio")
        String bio,
        String getImageUrlProfile,

        @Size(max = 30, message = "No máximo 30 caracteres na mensagem")
        String messageStatus
) {
}
