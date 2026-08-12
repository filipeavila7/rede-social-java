package com.example.demo.message.dto;

import com.example.demo.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record MessageReadResponse(
        NotificationType type,
        Long conversationId,
        Long readerId,
        LocalDateTime readAt
) {}