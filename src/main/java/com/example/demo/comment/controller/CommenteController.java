package com.example.demo.comment.controller;

import com.example.demo.comment.dto.CommentRequest;
import com.example.demo.comment.dto.CommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("comments")
public class CommenteController {

    private final CommentService service;


    // ========== GET ==========
    @GetMapping("/{postId}")
    public ResponseEntity<Page<CommentResponse>> getAllComments(
            @PathVariable Long postId,
            @PageableDefault(size = 14)
            Pageable pageable) {
        return ResponseEntity.ok(service.getAllPostCommentes(postId, pageable));

    }

    // ========== POST ==========
    @PostMapping("/{postId}/new")
    public ResponseEntity<CommentResponse> createCommente(
            @PathVariable Long postId, @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCommente(postId, request));
    }


    // ========== DELETE ==========
    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId){
        service.deleteCommente(commentId);
        return ResponseEntity.noContent().build();
    }
    
    
    
    
}
