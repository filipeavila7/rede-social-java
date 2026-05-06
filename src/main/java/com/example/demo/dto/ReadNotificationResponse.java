package com.example.demo.dto;

import java.util.List;

public record ReadNotificationResponse(
        String type,
        List<Long> messageIds
) {
}