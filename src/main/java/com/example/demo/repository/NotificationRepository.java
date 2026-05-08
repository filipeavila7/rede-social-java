package com.example.demo.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // verfica se ja existe notificação para aquele post
    boolean existsBySenderIdAndReceiverIdAndPostIdAndType(
            Long senderId,
            Long receiverId,
            Long postId,
            String type
    );
}
