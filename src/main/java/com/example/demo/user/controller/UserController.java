package com.example.demo.user.controller;

import com.example.demo.user.dto.UpdateUserRequest;
import com.example.demo.user.dto.UserRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.user.entity.User;
import com.example.demo.user.Service.UserService;

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

@RequiredArgsConstructor
@RestController
@RequestMapping("/users") // rota base para todos os endpoints
public class UserController {

    private final UserService service;


    // ========== GET ==========

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(service.getMe());
    }

    // ========== POST ==========

    @PostMapping("/new")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(request));
    }

    // ========== DELETE ==========

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser() {
        service.deleteUser();
        return ResponseEntity.noContent().build();
    }

    // ========== PUT ==========

    @PutMapping("/me")
    public ResponseEntity<UserResponse> uptadeUser(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(service.updateUser(request));
    }
}
