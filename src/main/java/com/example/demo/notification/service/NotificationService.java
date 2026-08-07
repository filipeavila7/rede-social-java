package com.example.demo.notification.service;

import com.example.demo.dto.NotificationGetResponse;
import com.example.demo.dto.NotificationRealtimeResponse;
import com.example.demo.notification.entity.Notification;
import com.example.demo.post.entity.Post;
import com.example.demo.user.entity.User;
import com.example.demo.exeptions.user.UserNotFoundException;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


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


    public NotificationRealtimeResponse createNotification(User loggedUser, Post post){
        // só notifica se não for o próprio post
        if (!post.getUser().getId().equals(loggedUser.getId())) {

            // salva a notificação no banco
            Notification notification = new Notification();
            notification.setType("COMMENT");
            notification.setContent(loggedUser.getName() + " comentou: " + comment.getContent());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setIsRead(false);
            notification.setSender(loggedUser);
            notification.setReceiver(post.getUser());
            notification.setPost(post);

            notificationRepository.save(notification);

            // cria a notificação para enviar via webSocket
            NotificationRealtimeResponse dto =
                    new NotificationRealtimeResponse(
                            "COMMENT",
                            user.getId(),
                            user.getNome(),
                            user.getUserName(),
                            user.getProfile() != null
                                    ? user.getProfile().getImageUrlProfile()
                                    : null,
                            postId,
                            null,
                            null,
                            notification.getContent(),
                            LocalDateTime.now()
                    );
    }
}
