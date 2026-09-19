package com.example.demo.search.mapper;

import com.example.demo.post.entity.Post;
import com.example.demo.search.dto.PostSuggestionResponse;
import com.example.demo.search.dto.ProfileSuggestionResponse;
import com.example.demo.search.dto.TagSuggestionResponse;
import com.example.demo.profile.entity.Profile;
import com.example.demo.tag.entity.Tag;
import com.example.demo.util.FileUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchMapper {

    private final FileUrlUtils fileUrlUtils;


    public ProfileSuggestionResponse toProfileSuggestion(Profile profile) {
        return new ProfileSuggestionResponse(
                profile.getId(),
                profile.getUser().getName(),
                profile.getUser().getUserName(),
               fileUrlUtils.toPublicUrl(profile.getImageUrlProfile())
        );
    }

    public PostSuggestionResponse toPostSuggestion(Post post) {
        return new PostSuggestionResponse(
                post.getId(),
                post.getTitle(),
                fileUrlUtils.toPublicUrl(post.getImageUrl())
        );
    }

    public TagSuggestionResponse toTagSuggestion(Tag tag) {
        return new TagSuggestionResponse(
                tag.getId(),
                tag.getName()
        );
    }
}