package com.example.demo.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // verfica se ja existe notificação para aquele post
    boolean existsBySenderIdAndReceiverIdAndPostIdAndType(
            Long senderId,
            Long receiverId,
            Long postId,
            String type
    );
    List<Notification> findByIdInAndReceiverId(List<Long> ids, Long receiverId);
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<Notification> findByReceiverIdAndIsReadFalse(Long receiverId);

}
