package com.example.demo.controller;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Post;
import com.example.demo.service.PostService;

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
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long seed) {

        long resolvedSeed = (seed != null) ? seed : Math.abs(new Random().nextLong() % 1_000_000);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Feed-Seed", String.valueOf(resolvedSeed));
        headers.add("Access-Control-Expose-Headers", "X-Feed-Seed"); // ← ESSENCIAL pro front ler o header

        return ResponseEntity.ok()
                .headers(headers)
                .body(service.getAllPosts(page, size, resolvedSeed));
    }
    // /posts/search
    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> searchPosts(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ResponseEntity.ok(service.searchPosts(termo, page, size));
    }


    @GetMapping("/search/suggestions")
    public ResponseEntity<List<String>> searchPostSuggestions(
            @RequestParam String termo
    ) {
        return ResponseEntity.ok(service.searchPostSuggestions(termo));
    }

    // /posts/{postId}
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId){
        return ResponseEntity.ok(service.getPostById(postId));
    }

    // /posts/user/me
    // retornar todos os posts do usuario logado
    @GetMapping("/user/me")
    public ResponseEntity<Page<PostResponse>> getPostByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(service.getMyPosts(page, size));
    }

    // /posts/user?userName=nomeDoUsuario
    // retorna todos os posts de um outro usuário com base no userName enviado na URL
    // @RequestParam captura o parâmetro da requisição GET diretamente da URL
    @GetMapping("/user")
    public ResponseEntity<Page<PostResponse>> getPostsByUserName(
            @RequestParam String userName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(service.getPostsByUserName(userName, page, size));
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
    // /post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostDto post) {
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
