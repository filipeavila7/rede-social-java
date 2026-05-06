package com.example.demo.dto;

// DTO para devolver mensagens com dados do remetente (nome e foto).
public record MessageResponse(
    Long id,
    Long conversationId,
    Long senderId,
    String senderName,
    String senderPhoto,
    String content,
    String createdAt,
     String readAt
) {}
