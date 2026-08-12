package com.example.demo.notification.controller;


import com.example.demo.notification.dto.NotificationGetResponse;
import com.example.demo.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<NotificationGetResponse>> getMyNotifications(
            @PageableDefault(size = 12)
            Pageable pageable
    ){

        return ResponseEntity.ok(notificationService.getMyNotifications(pageable));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMany(@RequestBody List<Long> ids) {
        notificationService.deleteNotifications(ids);
        return ResponseEntity.noContent().build();
    }


}
