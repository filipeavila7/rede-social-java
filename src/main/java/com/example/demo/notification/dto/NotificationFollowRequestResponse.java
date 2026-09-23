package com.example.demo.notification.dto;

import com.example.demo.followRequest.entity.FollowRequestStatus;
import com.example.demo.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationFollowRequestResponse(
        Long requestId,

        NotificationType type,

        Long senderId,

        FollowRequestStatus status,

        String senderName,

        String senderUserName,

        String senderPhoto,

        String content,

        LocalDateTime createdAt

) {
}