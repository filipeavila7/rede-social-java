package com.example.demo.search.dto;

import java.util.List;

public record SearchSuggestionsResponse(
        List<ProfileSuggestionResponse> profiles,
        List<PostSuggestionResponse> posts,
        List<TagSuggestionResponse> tags
) {
}