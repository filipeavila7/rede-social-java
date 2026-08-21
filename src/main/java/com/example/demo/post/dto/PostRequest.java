package com.example.demo.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.List;

public record PostRequest(
        @NotBlank
        @Size(max = 40, message = "No máximo 40 caracteres" )
        String title,

        // opcional
        @Size(max = 250, message = "No máximo 250 caracteres")
        String description,

        @NotBlank
        String imageUrl,

        @Size(max = 5, message = "É permitido no máximo 5 tags")
        List<String> tags
) {}