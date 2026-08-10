package com.example.demo.conversation.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.conversation.dto.ConversationResponse;
import com.example.demo.conversation.service.ConversationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    private final ConversationService service;


    @GetMapping("/my")
    public ResponseEntity<Page<ConversationResponse>> getMyContacts(
            @PageableDefault(size = 12)
            Pageable pageable
    ) {
    return ResponseEntity.ok(service.getMyConversations(pageable));
}

    @PostMapping("/new/{otherUserId}")
    public ResponseEntity<ConversationResponse> openConversation(@PathVariable Long otherUserId){
        return ResponseEntity.ok(service.openConversation(otherUserId));
    }
}
