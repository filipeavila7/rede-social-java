package com.example.demo.notification.dto;

import com.example.demo.notification.entity.NotificationType;
import com.example.demo.post.dto.PostSummaryResponse;

import java.time.LocalDateTime;

public record NotificationGetResponse(
        Long id,
        NotificationType type,
        String content,
        Boolean isRead,
        LocalDateTime createdAt,
        Long senderId,
        String senderName,
        String senderUserName,
        String senderPhoto,
        PostSummaryResponse post
) {
}
