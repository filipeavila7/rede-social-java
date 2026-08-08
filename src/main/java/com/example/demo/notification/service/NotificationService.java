package com.example.demo.notification.service;

import com.example.demo.dto.NotificationGetResponse;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.notification.dto.NotificationChatResponse;
import com.example.demo.notification.dto.NotificationPostResponse;
import com.example.demo.notification.entity.Notification;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.notification.mapper.NotificationMapper;
import com.example.demo.post.entity.Post;
import com.example.demo.service.WebSocketService;
import com.example.demo.user.entity.User;
import com.example.demo.exeptions.user.UserNotFoundException;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final GlobalHelperService globalHelperService;
    private final WebSocketService webSocketService;


    public List<NotificationGetResponse> getMyNotifications() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);


        List<Notification> notifications =
                notificationRepository
                        .findByReceiverIdOrderByCreatedAtDesc(user.getId());

        return notifications.stream().map(notification -> {

            NotificationGetResponse dto = new NotificationGetResponse(
                    notification.getId(),
                    notification.getType(),
                    notification.getContent(),
                    notification.getIsRead(),
                    notification.getCreatedAt(),
                    notification.getSender().getId(),
                    notification.getSender().getNome(),
                    notification.getSender().getUserName(),
                    notification.getSender().getProfile() != null
                            ? notification.getSender().getProfile().getImageUrlProfile()
                            : null,
                    notification.getPost() != null
                            ? notification.getPost().getId()
                            : null
            );

            return dto;

        }).toList();
    }


    public void deleteNotifications(List<Long> ids) {

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        List<Notification> toDelete =
                notificationRepository.findByIdInAndReceiverId(ids, user.getId());

        notificationRepository.deleteAll(toDelete);
    }

    // TODO criar metodos para notificações de mensagens no chat e de novos seguidores e arrumar essa service bizarra

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

}
