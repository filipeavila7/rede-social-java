package com.example.demo.websocket;

import com.example.demo.message.dto.ConversationUpdateResponse;
import com.example.demo.message.dto.MessageReadResponse;
import com.example.demo.notification.dto.NotificationChatResponse;
import com.example.demo.notification.dto.NotificationCommentResponse;
import com.example.demo.notification.dto.NotificationFollowResponse;
import com.example.demo.notification.dto.NotificationPostResponse;
import com.example.demo.message.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    // ler mensagens em tempo real
    public void sendMessageReadToUser(
            Long userId,
            MessageReadResponse response
    ) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                response
        );
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


    public void sendCommentNotificationToUser(Long userId, NotificationCommentResponse notification) {
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

    public void sendFollowNotificationToUser(Long userId, NotificationFollowResponse notification){
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