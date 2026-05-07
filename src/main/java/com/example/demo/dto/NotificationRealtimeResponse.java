package com.example.demo.dto;

import java.time.LocalDateTime;

public record NotificationRealtimeResponse(

        String type,

        Long senderId,

        String senderName,

        String senderPhoto,

        Long postId,

        Long conversationId,

        Long messageId,

        String content,

        LocalDateTime createdAt

) {
}