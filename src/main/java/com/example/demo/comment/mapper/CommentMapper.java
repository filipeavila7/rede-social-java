package com.example.demo.comment.mapper;

import com.example.demo.comment.dto.CommentDetails;
import com.example.demo.comment.entity.Comment;
import com.example.demo.comment.dto.CommentResponse;
import com.example.demo.post.mapper.PostMapper;
import com.example.demo.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentMapper {
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    public CommentResponse toCommentResponse(
            Comment c,
            CommentDetails details
    ) {
        return new CommentResponse(
                c.getId(),
                c.getContent(),
                c.getCreatedAt(),
                userMapper.toUserResponse(c.getUser()),
                postMapper.toPostSumaryResponse(c.getPost()),
                details.likedByMe(),
                details.hasReplies(),
                details.replyCount(),
                details.likeCount(),
                c.getParentComment() != null
                        ? c.getParentComment().getUser().getUserName()
                        : null
        );
    }
}

