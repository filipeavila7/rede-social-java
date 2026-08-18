package com.example.demo.post.controller;

import com.example.demo.feed.service.FeedService;
import com.example.demo.post.dto.PostRequest;
import com.example.demo.post.dto.PostDetaisResponse;
import com.example.demo.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.post.entity.Post;
import com.example.demo.post.service.PostService;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    public final PostService service;
    public final FeedService feedService;

    // ========== GET ==========

    // TODO - mover essa rota para o controller de feed
    @GetMapping
    public ResponseEntity<Page<PostDetaisResponse>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(feedService.getFeed(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostDetaisResponse>> searchPosts(
            @RequestParam String termo,
            @PageableDefault(size = 12)
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.searchPosts(termo, pageable));
    }

    @GetMapping("/search/suggestions")
    public ResponseEntity<List<String>> searchPostSuggestions(
            @RequestParam String termo
    ) {
        return ResponseEntity.ok(service.searchPostSuggestions(termo));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetaisResponse> getPostById(@PathVariable Long postId){
        return ResponseEntity.ok(service.getPostById(postId));
    }

    @GetMapping("/user/me")
    public ResponseEntity<Page<PostDetaisResponse>> getPostByUser(
            @PageableDefault(size = 12)
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.getMyPosts(pageable));
    }

    // ver posts de outros usuarios
    @GetMapping("/user")
    public ResponseEntity<Page<PostDetaisResponse>> getPostsByUserName(
            @RequestParam String userName,
            @PageableDefault(size = 12)
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.getPostsByUserName(userName, pageable));
    }


    // retorna a quantidade total de posts de um usuario pelo id
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getPostsCountByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getPostsCountByUserId(userId));
    }


    // retorna todos os likes e comentarios de um post pelo seu id
    // TODO - POSSÍVEL ROTA REDUNDANTE
    @GetMapping("/{postId}/stats")
    public ResponseEntity<Map<String, Long>> getPostStats(@PathVariable Long postId) {
        return ResponseEntity.ok(service.getPostStats(postId));
    }

    // ========== POST ==========

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest post) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPost(post));
    }

    // ========== DELETE ==========

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        service.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // ========== PUT ==========

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        Post updatedPost = service.updatePost(id, post);

        return ResponseEntity.ok(updatedPost);
    }

}
