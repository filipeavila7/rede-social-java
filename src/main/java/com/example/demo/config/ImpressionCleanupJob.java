package com.example.demo.config;

import com.example.demo.feed.service.PostImpressionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImpressionCleanupJob {

    private final PostImpressionService postImpressionService;

    @Scheduled(cron = "0 0 3 * * *") // 3h da manhã, uma vez por dia
    public void run() {
        postImpressionService.purgeOldImpressions();
    }
}