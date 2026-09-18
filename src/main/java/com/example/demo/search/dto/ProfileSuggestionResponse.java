package com.example.demo.search.dto;

public record ProfileSuggestionResponse(
        Long id,
        String name,
        String userName,
        String imageUrlProfile
) {
}