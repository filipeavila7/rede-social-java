package com.example.demo.likeStory.controller;

import com.example.demo.likeStory.service.LikeStoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
public class LikeStoryController {

    private final LikeStoryService likeStoryService;

    @PostMapping("/{storyId}/like")
    public ResponseEntity<Void> likeStory(
            @PathVariable Long storyId
    ) {
        likeStoryService.likeStory(storyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{storyId}/like")
    public ResponseEntity<Void> unlikeStory(
            @PathVariable Long storyId
    ) {
        likeStoryService.unlikeStory(storyId);
        return ResponseEntity.noContent().build();
    }
}