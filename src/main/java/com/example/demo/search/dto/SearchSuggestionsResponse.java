package com.example.demo.search.dto;

import java.util.List;

public record SearchSuggestionsResponse(
        List<ProfileSuggestionResponse> profiles,
        List<String> posts,
        List<TagSuggestionResponse> tags
) {
}