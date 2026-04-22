package com.example.demo.dto;

public record PostSummaryResponse(
        Long id,
        String content,
        String imageUrl
) {}