package com.example.demo.notification.dto;

import com.example.demo.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationPostResponse(

        NotificationType type,

        Long senderId,

        String senderName,

        String senderUserName,

        String senderPhoto,

        Long postId,

        String content,

        LocalDateTime createdAt

) {
}