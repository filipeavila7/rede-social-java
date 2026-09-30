package com.example.demo.closeFriends.controller;

import com.example.demo.closeFriends.dto.CloseFriendsRequest;
import com.example.demo.closeFriends.dto.CloseFriendsResponse;
import com.example.demo.closeFriends.service.CloseFriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/close-friends")
@RequiredArgsConstructor
public class CloseFriendsController {

    private final CloseFriendsService closeFriendsService;

    // buscar meus melhores amigos
    @GetMapping
    public ResponseEntity<Page<CloseFriendsResponse>> getMyCloseFriends(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                closeFriendsService.getMyCloseFriends(pageable)
        );
    }

    // adicionar usuários aos melhores amigos
    @PostMapping
    public ResponseEntity<List<CloseFriendsResponse>> addUsersInCloseFriends(
            @RequestBody CloseFriendsRequest request
    ) {
        return ResponseEntity.ok(
                closeFriendsService.addUsersInCloseFriends(request)
        );
    }

    // remover usuários dos melhores amigos
    @DeleteMapping
    public ResponseEntity<List<CloseFriendsResponse>> removeUsersFromCloseFriends(
            @RequestBody CloseFriendsRequest request
    ) {
        return ResponseEntity.ok(
                closeFriendsService.removeUsersFromCloseFriends(request)
        );
    }
}