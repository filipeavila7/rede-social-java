package com.example.demo.message.dto;

public record UnreadCountResponse(
        Long conversationId,
        Long unreadCount
) {}