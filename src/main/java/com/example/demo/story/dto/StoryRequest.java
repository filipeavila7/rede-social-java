package com.example.demo.story.dto;

import com.example.demo.story.entity.StoryVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StoryRequest(

        @NotBlank
        String imageUrl,

        @NotNull
        StoryVisibility visibility,

        @Size(max = 100)
        String description
) {
}
