package com.example.demo.service;

import com.example.demo.dto.NotificationResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dto.MessageResponse;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // enviar mensagem para alguem em tempo real
    public void sendMessageToUser(Long receiverId, MessageResponse response) {
        messagingTemplate.convertAndSend("/topic/messages/" + receiverId, response);
    }

    // notificar o outro usuario
    public void sendNotificationToUser(Long userId, NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }
}