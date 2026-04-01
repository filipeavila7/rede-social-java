package com.example.demo.controller;

import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/users") // rota base para todos os endpoints
public class UserController {
    private final UserRepository userRepository;
    private final UserService service;

    // injeção da service no construtor
    public UserController(UserService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    // GET
    @GetMapping // ResponseEntity para retornar uma resposta http completa
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // GET /users/me
    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.getMe(email));
    }

    // POST
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = service.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // DELETE /users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // PUT /users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<User> uptadeUser(@PathVariable Long id, @RequestBody User user) {
        User usuarioAtualizado = service.uptadeUser(id, user);
        return ResponseEntity.ok(usuarioAtualizado);
    }
}
