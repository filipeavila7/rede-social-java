package com.example.demo.service;

import com.example.demo.dto.NotificationGetResponse;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }


    public List<NotificationGetResponse> getMyNotifications() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email);



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

        User user = userRepository.findByEmail(email);

        List<Notification> toDelete =
                notificationRepository.findByIdInAndReceiverId(ids, user.getId());

        notificationRepository.deleteAll(toDelete);
    }
}
