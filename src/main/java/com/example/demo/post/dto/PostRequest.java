package com.example.demo.post.dto;

import lombok.NonNull;

import java.util.List;

public record PostRequest(
        @NonNull
        String content,

        String description,

        @NonNull
        String imageUrl,

        List<Long> tagIds
) {}