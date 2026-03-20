package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Commente;
import com.example.demo.service.CommentService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/posts")
public class CommenteController {

    private final CommentService service;

    public CommenteController(CommentService service) {
        this.service = service;
    }


    // POST
    // /{postId}/comments
    // recebe o objeto do comentario e o id do post para comentar
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Commente> createCommente(@PathVariable Long postId, @RequestBody Commente commente) {
        Commente created = service.createCommente(postId, commente);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    //GET
    // /{postId}/comments
    // retorna todos os comentarios de um post pelo id dele
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Commente>> getAllComments(@PathVariable Long postId) {
        return ResponseEntity.ok(service.getAllPostCommentes(postId));
    }


    // DELETE
    // /{commentId}/comments
    // 
    @DeleteMapping("/{commentId}/comments")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId){
        service.deleteCommente(commentId);
        return ResponseEntity.noContent().build();
    }
    
    
    
    
}
