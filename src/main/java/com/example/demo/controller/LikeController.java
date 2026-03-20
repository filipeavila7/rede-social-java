package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Like;
import com.example.demo.service.LikeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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

    @DeleteMapping
    public ResponseEntity<Void> unlikePost(@){

    }
    

}



