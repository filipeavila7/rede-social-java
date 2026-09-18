package com.example.demo.search.dto;

import com.example.demo.post.dto.PostDetaisResponse;
import com.example.demo.profile.dto.ProfileResponse;
import org.springframework.data.domain.Page;

public record SearchResponse(
        Page<ProfileResponse> profiles,
        Page<PostDetaisResponse> posts
) {
}