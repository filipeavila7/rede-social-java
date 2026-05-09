package com.example.demo.dto;

import java.time.LocalDateTime;

public record NotificationGetResponse(
        Long id,
        String type,
        String content,
        Boolean isRead,
        LocalDateTime createdAt,
        Long senderId,
        String senderName,
        String senderUserName,
        String senderPhoto,
        Long postId
) {
}
