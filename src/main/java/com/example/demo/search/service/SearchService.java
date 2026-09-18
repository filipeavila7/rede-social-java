package com.example.demo.search.service;

import com.example.demo.post.dto.PostDetaisResponse;

import com.example.demo.post.mapper.PostMapper;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.profile.dto.ProfileResponse;

import com.example.demo.profile.mapper.ProfileMapper;
import com.example.demo.profile.repository.ProfileRepository;
import com.example.demo.search.dto.*;
import com.example.demo.search.mapper.SearchMapper;
import com.example.demo.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    private final ProfileMapper profileMapper;
    private final PostMapper postMapper;

    private final SearchMapper searchMapper;

    // buscar posts e usuarios
    public SearchResponse search(String q, Pageable pageable) {

        String query = q.trim();

        Page<ProfileResponse> profiles = profileRepository
                .searchProfiles(query, pageable)
                .map(profileMapper::toProfileResponse);

        Page<PostDetaisResponse> posts = postRepository
                .searchPosts(query, pageable)
                .map(postMapper::toPostDetaisResponse);

        return new SearchResponse(
                profiles,
                posts
        );
    }


    public SearchSuggestionsResponse suggestions(String q) {

        String query = q.trim();

        List<ProfileSuggestionResponse> profiles =
                profileRepository
                        .searchProfileSuggestions(
                                query,
                                PageRequest.of(0, 5)
                        )
                        .stream()
                        .map(searchMapper::toProfileSuggestion)
                        .toList();

        List<PostSuggestionResponse> posts =
                postRepository
                        .searchPostSuggestions(
                                query,
                                PageRequest.of(0, 5)
                        )
                        .stream()
                        .map(searchMapper::toPostSuggestion)
                        .toList();

        List<TagSuggestionResponse> tags =
                tagRepository
                        .searchTagSuggestions(
                                query,
                                PageRequest.of(0, 5)
                        )
                        .stream()
                        .map(searchMapper::toTagSuggestion)
                        .toList();

        return new SearchSuggestionsResponse(
                profiles,
                posts,
                tags
        );
    }
}