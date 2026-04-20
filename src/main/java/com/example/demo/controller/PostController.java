package com.example.demo.controller;

import com.example.demo.dto.PostResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Post;
import com.example.demo.service.PostService;

import java.util.List;
import java.util.Map;

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
@RequestMapping("/posts")
public class PostController {

    public final PostService service;

    // injetar a service no construtor
    public PostController(PostService service) {
        this.service = service;
    }

    // ROTAS GET
    // /posts
    @GetMapping()
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(service.getAllPosts());
    }

    // /posts/{postId}
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId){
        return ResponseEntity.ok(service.getPostById(postId));
    }

    // /posts/user/me
    // retornar todos os posts do usuario logado
    @GetMapping("/user/me")
    public ResponseEntity<List<Post>> getPostByUser() {
        return ResponseEntity.ok(service.getMyPosts());
    }

    // /posts/user?email=usuario@email.com
    // retornar os post de um outro usuario pelo email
    // quando uma rota get recebe um parametro, ele vai na url, no caso o email
    @GetMapping("/user")
    public ResponseEntity<List<Post>> getPostsByUserEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.getPostsByUserEmail(email));
    }

    // /posts/user/{userId}/count
    // retorna a quantidade total de posts de um usuario pelo id
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getPostsCountByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getPostsCountByUserId(userId));
    }


    // retorna todos os likes e comentarios de um post pelo seu id
    @GetMapping("/{postId}/stats")
    public ResponseEntity<Map<String, Long>> getPostStats(@PathVariable Long postId) {
        return ResponseEntity.ok(service.getPostStats(postId));
    }

    // POST
    // criar post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = service.createPost(post);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        service.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // Put
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        Post updatedPost = service.updatePost(id, post);

        return ResponseEntity.ok(updatedPost);
    }

}
