package com.example.demo.dto;

public record UnreadCountResponse(
        Long conversationId,
        Long unreadCount
) {}