package com.example.demo.notification.dto;

import com.example.demo.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationStoryResponse(
        NotificationType type,

        Long senderId,

        String senderName,

        String senderUserName,

        String senderPhoto,

        Long storyId,

        String content,

        LocalDateTime createdAt
) {
}
