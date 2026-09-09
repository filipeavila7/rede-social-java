package com.example.demo.post.dto;

import java.util.List;

public record PostWithRelatedResponse(
        PostDetaisResponse post,
        List<PostDetaisResponse> relatedPosts
) {}
