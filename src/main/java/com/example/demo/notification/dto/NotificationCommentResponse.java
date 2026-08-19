package com.example.demo.notification.dto;

import com.example.demo.comment.entity.Comment;
import com.example.demo.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationCommentResponse(

        NotificationType type,

        Long senderId,

        String senderName,

        String senderUserName,

        String senderPhoto,

        Long postId,

        String content,

        LocalDateTime createdAt,

        Comment comment

) {
}