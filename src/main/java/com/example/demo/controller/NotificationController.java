package com.example.demo.controller;


import com.example.demo.dto.NotificationGetResponse;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping()
    public ResponseEntity<List<NotificationGetResponse>> getMyNotifications(){

        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMany(@RequestBody List<Long> ids) {
        notificationService.deleteNotifications(ids);
        return ResponseEntity.noContent().build();
    }


}
