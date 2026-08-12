package com.example.demo.message.controller;

import java.util.List;

import com.example.demo.message.dto.MessageRequest;
import com.example.demo.message.dto.UnreadCountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.message.dto.MessageResponse;
import com.example.demo.message.entity.Message;
import com.example.demo.message.service.MessageService;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @PostMapping("/{receiverId}")
    public ResponseEntity<MessageResponse> sendMessage(
        @PathVariable Long receiverId,
        @Valid @RequestBody MessageRequest request
    ) {
        MessageResponse created = service.sendMessage(receiverId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @PathVariable Long conversationId,
            @PageableDefault(size = 50)
            Pageable pageable
    ){
        return ResponseEntity.ok(service.getMessages(conversationId, pageable));
    }


    @GetMapping("/conversations/unread")
    public ResponseEntity<List<UnreadCountResponse>> getUnread() {
        return ResponseEntity.ok(service.getUnreadConversations());
    }




    @PostMapping("/conversation/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long conversationId) {
        service.markConversationAsRead(conversationId);
        return ResponseEntity.ok().build();
    }


}
