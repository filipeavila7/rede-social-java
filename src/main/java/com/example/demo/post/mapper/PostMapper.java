package com.example.demo.post.mapper;

import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.post.dto.PostSummaryResponse;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.post.dto.PostDetaisResponse;
import com.example.demo.post.dto.PostResponse;
import com.example.demo.post.entity.Post;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {
    private final UserMapper userMapper;
    private final GlobalHelperService globalHelperService;

    public PostDetaisResponse toPostDetaisResponse(Post post, Long loggedUserId) {

        long likesCount = globalHelperService.countLikeByPostId(post.getId());
        long commentsCount = globalHelperService.countCommentBypostId(post.getId());
        boolean likedByMe = globalHelperService.existsLikeInPost(loggedUserId, post.getId());

        return new PostDetaisResponse(
                post.getId(),
                post.getTitle(),
                post.getImageUrl(),
                userMapper.toUserResponse(post.getUser()),
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
                userMapper.toUserResponse(post.getUser()),
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
