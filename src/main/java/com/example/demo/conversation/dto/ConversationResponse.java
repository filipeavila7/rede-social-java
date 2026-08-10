package com.example.demo.conversation.dto;

public record ConversationResponse(
    Long conversationId,
    Long otherUserId,
    String otherUserName,
    String otherUserPhoto,
    String lastMessage,
    String lastMessageAt
 ) {}
