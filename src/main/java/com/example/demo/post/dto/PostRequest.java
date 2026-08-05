package com.example.demo.post.dto;

import java.util.List;

public record PostRequest(
        String content,
        String description,
        String imageUrl,
        List<Long> tagIds
) {}