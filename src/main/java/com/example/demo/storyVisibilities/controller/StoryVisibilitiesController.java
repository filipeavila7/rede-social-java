package com.example.demo.storyVisibilities.controller;

import com.example.demo.storyVisibilities.dto.StoryVisibilitiesResponse;
import com.example.demo.storyVisibilities.service.StoryVisibilitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stories")
public class StoryVisibilitiesController {
    private final StoryVisibilitiesService storyVisibilitiesService;

    @PostMapping("/{storyId}/view")
    public ResponseEntity<Void> createStoryVisibility(
            @PathVariable Long storyId
    ) {
        storyVisibilitiesService.createStoryVisibility(storyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{storyId}/views")
    public ResponseEntity<Page<StoryVisibilitiesResponse>> getStoryVisibilities(
            @PathVariable Long storyId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                storyVisibilitiesService.getStoryVisibilities(
                        storyId,
                        pageable
                )
        );
    }
}