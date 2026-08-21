package com.example.demo.comment.dto;

public record CommentDetails(
        boolean likedByMe,
        boolean hasReplies,
        long replyCount,
        long likeCount
) {
}
