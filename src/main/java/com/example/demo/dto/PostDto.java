package com.example.demo.dto;

import java.util.List;

public record PostDto(
        String content,
        String description,
        String imageUrl,
        List<Long> tagIds
) {}