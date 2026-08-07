package com.example.demo.notification.mapper;

import com.example.demo.notification.dto.NotificationRealtimeResponse;
import com.example.demo.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationRealtimeResponse toNotificationRealtimeResponse(Notification n){
        return new NotificationRealtimeResponse(
                n.getType(),
                n.getSender().getId(),
                n.getSender().getName(),
                n.getSender().getUserName(),
                n.getSender().getProfile().getImageUrlProfile(),
                n.getPost() != null ?  n.getPost().getId() : null,
                n.getContent(),
                n.getCreatedAt()
        );
    }
}
