package com.example.demo.message.dto;

import java.time.LocalDateTime;

public record ConversationUpdateResponse(
        Long conversationId,
        String lastMessage,
        LocalDateTime lastMessageAt,
        Long senderId
) {}