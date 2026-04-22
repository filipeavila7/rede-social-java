package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Like;
import com.example.demo.service.LikeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/posts")
public class LikeController {

    public final LikeService service;

    public LikeController(LikeService service) {
        this.service = service;
    }

    

    // POST
    // /posts/{postId}/likes
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Like> likePost(@PathVariable Long postId) {
        Like likedPost = service.likePost(postId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(likedPost);
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId){
        service.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/liked")
    public ResponseEntity<Boolean> hasLiked(@PathVariable Long postId) {
        boolean liked = service.hasUserLikedPost(postId);
        return ResponseEntity.ok(liked);
    }
    

}



