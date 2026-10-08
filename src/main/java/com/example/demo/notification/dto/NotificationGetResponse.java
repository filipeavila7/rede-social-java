package com.example.demo.notification.dto;

import com.example.demo.followRequest.entity.FollowRequestStatus;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.post.dto.PostSummaryResponse;
import com.example.demo.story.dto.StorySummaryResponse;

import java.time.LocalDateTime;

public record NotificationGetResponse(
        Long id,
        NotificationType type,
        String content,
        Boolean isRead,
        LocalDateTime createdAt,
        Long senderId,
        String senderName,
        String senderUserName,
        String senderPhoto,
        PostSummaryResponse post,
        Long followRequestId,
        FollowRequestStatus followRequestStatus,
        StorySummaryResponse storySummaryResponse
) {
}
