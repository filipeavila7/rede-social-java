package com.example.demo.conversation.dto;

import java.time.LocalDateTime;

public record ConversationResponse(
    Long conversationId,
    Long otherUserId,
    String otherUserName,
    String otherUserUsername,
    String otherUserPhoto,
    String lastMessage,
    LocalDateTime lastMessageAt
 ) {}
