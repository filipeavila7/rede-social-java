package com.example.demo.story.dto;

import com.example.demo.story.entity.StoryVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StoryTextRequest(
        @Size(max = 100)
        String text,

        @NotNull
        StoryVisibility visibility,

        @NotBlank
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") // valida hexa decimal
        String backgroundColor
) {
}
