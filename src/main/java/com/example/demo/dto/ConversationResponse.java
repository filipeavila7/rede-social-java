package com.example.demo.dto;

public record ConversationResponse(
    Long conversationId,
    Long otherUserId,
    String otherUserName,
    String otherUserPhoto,
    String lastMessage,
    String lastMessageAt
 ) {}
