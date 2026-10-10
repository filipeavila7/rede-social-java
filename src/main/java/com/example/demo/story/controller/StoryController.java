package com.example.demo.story.controller;

import com.example.demo.story.dto.MyStorySummaryResponse;
import com.example.demo.story.dto.StoryRequest;
import com.example.demo.story.dto.StoryResponse;
import com.example.demo.story.dto.StoryTextRequest;
import com.example.demo.story.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;


    // mostrar todos os stories ativos de um usuário
    @GetMapping("/user/{userName}")
    public ResponseEntity<Page<StoryResponse>> getUserStories(
            @PathVariable String userName,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                storyService.getUserStories(userName, pageable)
        );
    }

    // histórico de story
    @GetMapping("/me/history")
    public ResponseEntity<Page<StoryResponse>> getStoryHistory(
            Pageable pageable
    ){
        return ResponseEntity.ok(storyService.getHitoryStories(pageable));
    }


    @GetMapping("/{storyId}/image")
    public ResponseEntity<Resource> getStoryImage(
            @PathVariable Long storyId
    ) {
        Resource image = storyService.getStoryImage(storyId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @GetMapping("/me")
    public List<MyStorySummaryResponse> getMyStories() {
        return storyService.getMyStories();
    }

    // criar story de imagem
    @PostMapping
    public ResponseEntity<StoryResponse> createStory(
            @RequestBody StoryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storyService.createStory(request));
    }

    // criar story de texto
    @PostMapping("/text")
    public ResponseEntity<StoryResponse> createTextStory(
            @RequestBody StoryTextRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storyService.createTextStory(request));
    }

    // deletar story
    @DeleteMapping("/{storyId}")
    public ResponseEntity<Void> deleteStory(@PathVariable Long storyId) {
        storyService.deleteStoryById(storyId);
        return ResponseEntity.noContent().build();
    }


}