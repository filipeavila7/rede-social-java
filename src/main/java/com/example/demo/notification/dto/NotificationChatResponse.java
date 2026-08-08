package com.example.demo.notification.dto;

import com.example.demo.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationChatResponse(
        NotificationType type,

        Long senderId,

        String senderName,

        String senderUserName,

        String senderPhoto,

        Long conversationId,

        Long messageId,

        String content,

        LocalDateTime createdAt
) {
}
