package com.example.demo.notification.mapper;

import com.example.demo.comment.mapper.CommentMapper;
import com.example.demo.followRequest.entity.FollowRequest;
import com.example.demo.notification.dto.*;
import com.example.demo.notification.entity.Notification;
import com.example.demo.post.mapper.PostMapper;
import com.example.demo.user.entity.User;
import com.example.demo.util.FileUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final FileUrlUtils fileUrlUtils;
    private final PostMapper postMapper;


    public NotificationFollowRequestResponse
    toNotificationFollowRequestResponse(Notification notification) {

        FollowRequest request = notification.getFollowRequest();

        User requester = request.getRequester();

        return new NotificationFollowRequestResponse(
                request.getId(),
                notification.getType(),
                requester.getId(),
                request.getStatus(),
                requester.getName(),
                requester.getUserName(),
                fileUrlUtils.toPublicUrl(requester.getProfile().getImageUrlProfile()),
                notification.getContent(),
                notification.getCreatedAt()
        );
    }


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
                n.getPost() != null ? postMapper.toPostSumaryResponse(n.getPost()) : null,
                n.getFollowRequest() != null ? n.getFollowRequest().getId() : null,
                n.getFollowRequest() != null ? n.getFollowRequest().getStatus() : null

        );
    }
}
