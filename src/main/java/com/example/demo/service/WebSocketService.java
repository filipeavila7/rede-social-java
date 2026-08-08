package com.example.demo.service;

import com.example.demo.dto.ConversationUpdateResponse;
import com.example.demo.notification.dto.NotificationChatResponse;
import com.example.demo.notification.dto.NotificationPostResponse;
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

    // notificações do tipo (LIKE, COMMENT)
    public void sendPostNotificationToUser(Long userId, NotificationPostResponse notification) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                notification
        );
    }

    public void sendChatNotificationToUser(Long userId, NotificationChatResponse notification){
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                notification
        );
    }


    // atualizar fora da conversation
    public void sendConversationUpdate(Long userId, ConversationUpdateResponse response) {
        messagingTemplate.convertAndSend(
                "/topic/conversations/" + userId,
                response
        );
    }
}