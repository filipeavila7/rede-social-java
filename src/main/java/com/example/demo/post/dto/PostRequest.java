package com.example.demo.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.List;

public record PostRequest(
        @NotBlank
        @Size(max = 40)
        String title,

        // opcional
        String description,

        @NotBlank
        String imageUrl,

        List<Long> tagIds
) {}