package com.example.demo.notification.mapper;

import com.example.demo.notification.dto.*;
import com.example.demo.notification.entity.Notification;
import com.example.demo.post.mapper.PostMapper;
import com.example.demo.util.FileUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final FileUrlUtils fileUrlUtils;
    private final PostMapper postMapper;

    public NotificationPostResponse toNotificationPostResponse(Notification n){
        return new NotificationPostResponse(
                n.getType(),
                n.getSender().getId(),
                n.getSender().getName(),
                n.getSender().getUserName(),
                n.getSender().getProfile().getImageUrlProfile() != null ?
                        fileUrlUtils.toPublicUrl(n.getSender().getProfile().getImageUrlProfile())  : null,
                n.getPost() != null ?  n.getPost().getId() : null,
                n.getContent(),
                n.getCreatedAt()
        );
    }

    public NotificationCommentResponse toNotificationCommentResponse(Notification n){
        return new NotificationCommentResponse(
                n.getType(),
                n.getSender().getId(),
                n.getSender().getName(),
                n.getSender().getUserName(),
                n.getSender().getProfile().getImageUrlProfile() != null ?
                        fileUrlUtils.toPublicUrl(n.getSender().getProfile().getImageUrlProfile())  : null,
                n.getPost() != null ?  n.getPost().getId() : null,
                n.getContent(),
                n.getCreatedAt(),
                n.getComment()
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
                    fileUrlUtils.toPublicUrl(n.getSender().getProfile().getImageUrlProfile())  : null,
                conversationId,
                messageId,
                n.getContent(),
                n.getCreatedAt()
        );
    }

    public NotificationFollowResponse toNotificationFollowResponse(Notification n){
        return new NotificationFollowResponse(
                n.getType(),
                n.getSender().getId(),
                n.getSender().getName(),
                n.getSender().getUserName(),
                n.getSender().getProfile().getImageUrlProfile() != null ?
                      fileUrlUtils.toPublicUrl(n.getSender().getProfile().getImageUrlProfile())  : null,
                n.getContent(),
                n.getCreatedAt()
        );
    }

    // parar de salvar o nome do usario no content e deixar por conta do q vem no dto
    public NotificationGetResponse toNotificationGetResponse(Notification n){
        return new NotificationGetResponse(
                n.getId(),
                n.getType(),
                n.getContent(),
                n.getIsRead(),
                n.getCreatedAt(),
                n.getSender().getId(),
                n.getSender().getName(),
                n.getSender().getUserName(),
                n.getSender().getProfile().getImageUrlProfile() != null ?
                     fileUrlUtils.toPublicUrl( n.getSender().getProfile().getImageUrlProfile())  : null,
                n.getPost() != null ? postMapper.toPostSumaryResponse(n.getPost()) : null

        );
    }
}
