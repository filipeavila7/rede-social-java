package com.example.demo.notification.repository;

import com.example.demo.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);
    List<Notification> findByReceiverIdAndIsReadFalse(Long receiverId);

}
