package com.example.demo.service;

import com.example.demo.dto.NotificationRealtimeResponse;
import com.example.demo.dto.MessageResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // CHAT (conversa)
    public void sendMessageToConversation(Long conversationId, MessageResponse response) {
        messagingTemplate.convertAndSend(
                "/topic/messages/conversation/" + conversationId,
                response
        );
    }

    // TUDO QUE NÃO É CHAT (LIKE, MESSAGE, READ, FOLLOW, ETC)
    public void sendNotificationToUser(Long userId, NotificationRealtimeResponse notification) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                notification
        );
    }
}