package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ConversationResponse;
import com.example.demo.entity.Conversation;
import com.example.demo.service.ConversationService;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private final ConversationService service;

    public ConversationController(ConversationService service) {
        this.service = service;
    }

    // GET /conversations/me
    @GetMapping("/me")
    public ResponseEntity<List<ConversationResponse>> getMyContacts() {
    return ResponseEntity.ok(service.getMyContacts());
}

    // GET /conversations/contacts (alias para /me)
    @GetMapping("/contacts")
    public ResponseEntity<List<ConversationResponse>> getMyContactsAlias() {
        return ResponseEntity.ok(service.getMyContacts());
    }

    // POST /conversations/{otherUserId}
    @PostMapping("/open/{otherUserId}")
    public ResponseEntity<ConversationResponse> openConversation(@PathVariable Long otherUserId){
        return ResponseEntity.ok(service.openConversation(otherUserId));
    }
}
