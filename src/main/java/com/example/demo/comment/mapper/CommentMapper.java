package com.example.demo.comment.mapper;

import com.example.demo.comment.entity.Comment;
import com.example.demo.comment.dto.CommentResponse;
import com.example.demo.comment.service.CommentService;
import com.example.demo.likeComents.service.LikeCommentService;
import com.example.demo.post.mapper.PostMapper;
import com.example.demo.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentMapper {
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final LikeCommentService likeCommentService;
    private final CommentService commentService;

    public CommentResponse toCommentResponse(Comment c){
        return new CommentResponse(
                c.getId(),
                c.getContent(),
                c.getCreatedAt(),
                userMapper.toUserResponse(c.getUser()),
                postMapper.toPostSumaryResponse(c.getPost()),
                likeCommentService.likeCommentByMe(c.getId()),
                commentService.exixstReplys(c.getId()),
                commentService.countReplys(c.getId()),
                likeCommentService.countCommentLike(c.getId())


        );
    }
}
