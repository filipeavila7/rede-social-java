package com.example.demo.like.controller;

import com.example.demo.like.dto.LikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import com.example.demo.like.entity.Like;
import com.example.demo.like.service.LikeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    public final LikeService service;

    // ========== GET ==========

    // TODO - POSSÍVEL ROTA REDUNDANTE
    @GetMapping("/{postId}/liked")
    public ResponseEntity<Boolean> hasLiked(@PathVariable Long postId) {
        boolean liked = service.hasUserLikedPost(postId);
        return ResponseEntity.ok(liked);
    }


    @GetMapping("/my")
    public ResponseEntity<Page<LikeResponse>> myLikedPosts(
            @PageableDefault(size = 12)
            Pageable pageable
    ){
        return ResponseEntity.ok().body(service.getMyLikedPost(pageable));
    }

    // ========== POST ==========

    @PostMapping("/{postId}/new")
    public ResponseEntity<LikeResponse> likePost(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createLike(postId));
    }

    // ========== DELETE ==========

    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId){
        service.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }


}



