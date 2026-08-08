package com.example.demo.notification.mapper;

import com.example.demo.notification.dto.NotificationChatResponse;
import com.example.demo.notification.dto.NotificationPostResponse;
import com.example.demo.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationPostResponse toNotificationPostResponse(Notification n){
        return new NotificationPostResponse(
                n.getType(),
                n.getSender().getId(),
                n.getSender().getName(),
                n.getSender().getUserName(),
                n.getSender().getProfile().getImageUrlProfile() != null ?
                        n.getSender().getProfile().getImageUrlProfile() : null,
                n.getPost() != null ?  n.getPost().getId() : null,
                n.getContent(),
                n.getCreatedAt()
        );
    }

    public NotificationChatResponse toNotificationChatResponse(
            Notification n,  Long conversationId, Long messageId){
        return new NotificationChatResponse(
                n.getType(),
                n.getSender().getId(),
                n.getSender().getName(),
                n.getSender().getUserName(),
                n.getSender().getProfile().getImageUrlProfile() != null ?
                        n.getSender().getProfile().getImageUrlProfile() : null,
                conversationId,
                messageId,
                n.getContent(),
                n.getCreatedAt()
        );
    }
}
