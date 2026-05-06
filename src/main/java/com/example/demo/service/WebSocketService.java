package com.example.demo.service;

import com.example.demo.dto.NotificationResponse;
import com.example.demo.dto.ReadNotificationResponse;
import com.example.demo.dto.MessageResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // CANAL DA CONVERSA (REALTIME CHAT)
    public void sendMessageToConversation(Long conversationId, MessageResponse response) {
        messagingTemplate.convertAndSend("/topic/messages/conversation/" + conversationId, response);
    }

    // NOTIFICAÇÃO PARA SIDEBAR/LISTA DE CONVERSAS
    public void sendNotificationToUser(Long userId, NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }

    // AVISO DE LEITURA
    public void sendReadStatusToUser(Long userId, ReadNotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/read-status/" + userId, notification);
    }
}