package com.example.demo.dto;

public record NotificationResponse(
        String type,
        Long conversationId,
        Long senderId,
        String senderName,
        String senderPhoto,
        String content
) {
}