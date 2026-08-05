package com.example.demo.post.mapper;

import com.example.demo.dto.UserResponse;
import com.example.demo.post.dto.PostDetaisResponse;
import com.example.demo.post.dto.PostResponse;
import com.example.demo.post.entity.Post;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public PostDetaisResponse toPostDetaisResponse(Post post, Long loggedUserId) {

        long likesCount = likeRepository.countByPostId(post.getId());
        long commentsCount = commentRepository.countByPostId(post.getId());
        boolean likedByMe = likeRepository.existsByUserIdAndPostId(loggedUserId, post.getId());

        return new PostDetaisResponse(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                new UserResponse(
                        post.getUser().getId(),
                        post.getUser().getNome(),
                        post.getUser().getProfile().getImageUrlProfile(),
                        post.getUser().getUserName()
                ),
                post.getCreatedAt(),
                post.getDescription(),
                post.getTags(),
                likesCount,
                commentsCount,
                likedByMe
        );
    }

    public PostResponse toPostResponse(Post post){
        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                new UserResponse(
                        post.getUser().getId(),
                        post.getUser().getNome(),
                        post.getUser().getProfile().getImageUrlProfile(),
                        post.getUser().getUserName()
                ),
                post.getCreatedAt(),
                post.getDescription(),
                post.getTags()
        );
    }

}
