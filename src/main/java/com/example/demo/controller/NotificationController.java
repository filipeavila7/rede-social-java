package com.example.demo.controller;


import com.example.demo.dto.NotificationGetResponse;
import com.example.demo.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
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
