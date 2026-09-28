package com.example.demo.story.dto;

import com.example.demo.story.entity.StoryVisibility;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StoryTextRequest(
        @Size(max = 100)
        String text,

        @NotNull
        StoryVisibility visibility
) {
}
