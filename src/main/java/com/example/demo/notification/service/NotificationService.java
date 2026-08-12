package com.example.demo.notification.service;

import com.example.demo.message.dto.MessageReadResponse;
import com.example.demo.notification.dto.NotificationGetResponse;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.notification.entity.Notification;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.notification.mapper.NotificationMapper;
import com.example.demo.post.entity.Post;
import com.example.demo.websocket.WebSocketService;
import com.example.demo.user.entity.User;
import com.example.demo.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final GlobalHelperService globalHelperService;
    private final WebSocketService webSocketService;

    // buscar as notificações do usuario logado
    public Page<NotificationGetResponse> getMyNotifications(Pageable pageable) {
        return notificationRepository.
                findByReceiverIdOrderByCreatedAtDesc(globalHelperService.getLoggedUser().getId(), pageable)
                .map(notificationMapper::toNotificationGetResponse);
    }


    public void deleteNotifications(List<Long> ids) {
        Set<Long> uniqueIds = new HashSet<>(ids);

        if (uniqueIds.size() != ids.size()) {
            throw new IllegalArgumentException("IDs duplicados");
        }

        List<Notification> toDelete =
                notificationRepository.findByIdInAndReceiverId(ids,
                        globalHelperService.getLoggedUser().getId());


        notificationRepository.deleteAll(toDelete);
    }


    // criar notificações que tem post (COMMENT E LIKE) e ja envia via webSocket
    public void createPostNotification(
            User loggedUser, User receiver, Post post, NotificationType type, String content) {

        // só notifica se não for o próprio post
        if (post.getUser().getId().equals(loggedUser.getId())) {
            return;
        }

        // cria a notificação
        Notification notification = globalHelperService.buildNotification(
                loggedUser, receiver, type, content
        );

        // relaciona com post
        notification.setPost(post);

        // salva
        notificationRepository.save(notification);

        // enviar notificação via webSocket
        webSocketService.sendPostNotificationToUser(receiver.getId(),
                notificationMapper.toNotificationPostResponse(notification));
    }


    // notificações de mensagens do chat, não salva no banco
    public void createChatNotification(
            User loggedUser, User receiver, NotificationType type, String content,
            Long conversationId, Long messageId
    ){
        // cria a notificação
        Notification notification = globalHelperService.buildNotification(
                loggedUser, receiver, type, content
        );

        // envia a notificação via webSocket
        webSocketService.sendChatNotificationToUser(receiver.getId(),
                notificationMapper.toNotificationChatResponse(notification, conversationId, messageId));
    }


    // criar notificação de novo seguidor
    public void createFollowNotification(
            User loggedUser, User receiver, NotificationType type, String content
    ){
        // cria a notificação
        Notification notification = globalHelperService.buildNotification(
                loggedUser, receiver, type, content
        );

        // salva
        notificationRepository.save(notification);


        // envia a notificação via webSocket
        webSocketService.sendFollowNotificationToUser(receiver.getId(),
                notificationMapper.toNotificationFollowResponse(notification));

    }


    // notificação em tempo real quando as mensagens são lidas
    public void sendMessageReadNotification(
            User reader,
            Long receiverId,
            Long conversationId,
            LocalDateTime readAt,
            NotificationType type
    ) {
        MessageReadResponse response = new MessageReadResponse(
                type,
                conversationId,
                reader.getId(),
                readAt
        );

        webSocketService.sendMessageReadToUser(
                receiverId,
                response
        );
    }

}
