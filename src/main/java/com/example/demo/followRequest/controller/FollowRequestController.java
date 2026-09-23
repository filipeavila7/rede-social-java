package com.example.demo.followRequest.controller;

import com.example.demo.followRequest.service.FollowRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follow-request")
@RequiredArgsConstructor
public class FollowRequestController {
    private final FollowRequestService followRequestService;

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<Void> acceptRequest(
            @PathVariable Long requestId
    ) {
        followRequestService.acceptRequest(requestId);

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long requestId
    ) {
        followRequestService.rejectRequest(requestId);

        return ResponseEntity.noContent().build();
    }
}
