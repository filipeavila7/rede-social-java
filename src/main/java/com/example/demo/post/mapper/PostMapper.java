package com.example.demo.post.mapper;

import com.example.demo.post.dto.PostSummaryResponse;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.post.dto.PostDetaisResponse;
import com.example.demo.post.dto.PostResponse;
import com.example.demo.post.entity.Post;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.like.repository.LikeRepository;
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
                post.getTitle(),
                post.getImageUrl(),
                new UserResponse(
                        post.getUser().getId(),
                        post.getUser().getName(),
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
                post.getTitle(),
                post.getImageUrl(),
                new UserResponse(
                        post.getUser().getId(),
                        post.getUser().getName(),
                        post.getUser().getProfile().getImageUrlProfile(),
                        post.getUser().getUserName()
                ),
                post.getCreatedAt(),
                post.getDescription(),
                post.getTags()
        );


    }

    public PostSummaryResponse toPostSumaryResponse(Post p){
        return new PostSummaryResponse(
                p.getId(),
                p.getTitle()
        );
    }

}
