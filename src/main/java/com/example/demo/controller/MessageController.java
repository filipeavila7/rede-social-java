package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.MessageResponse;
import com.example.demo.entity.Message;
import com.example.demo.service.MessageService;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    // POST /messages/{receiverId}
    // enviar mensagem
    @PostMapping("/{receiverId}")
    public ResponseEntity<MessageResponse> sendMessage(
        @PathVariable Long receiverId,
        @RequestBody Message body
    ) {
        MessageResponse created = service.sendMessage(receiverId, body.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /messages/conversation/{conversationId}
    // listar todas as mensagens de uma conversa
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long conversationId) {
        return ResponseEntity.ok(service.getMessages(conversationId));
    }




    @PostMapping("/conversation/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long conversationId) {
        service.markConversationAsRead(conversationId);
        return ResponseEntity.ok().build();
    }
}
