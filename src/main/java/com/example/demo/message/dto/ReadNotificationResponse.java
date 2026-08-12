package com.example.demo.message.dto;

import java.util.List;

public record ReadNotificationResponse(
        String type,
        List<Long> messageIds
) {
}