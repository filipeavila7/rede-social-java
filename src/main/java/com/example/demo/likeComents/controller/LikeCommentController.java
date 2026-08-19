package com.example.demo.likeComents.controller;

import com.example.demo.likeComents.service.LikeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/like-comment")
@RestController
public class LikeCommentController {
    private final LikeCommentService likeCommentService;


    @PostMapping("/{commentId}")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long commentId
    ){
        likeCommentService.likeComment(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> unLikeComment(
            @PathVariable Long commentId
    ){
        likeCommentService.deleteLikeComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
