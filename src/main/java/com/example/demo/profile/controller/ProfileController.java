package com.example.demo.profile.controller;

import java.util.List;

import com.example.demo.profile.dto.ProfileUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.example.demo.profile.dto.ProfileResponse;

import com.example.demo.profile.service.ProfileService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profiles")
public class ProfileController {
    private final ProfileService service;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile() {
        return ResponseEntity.ok(service.getMyProfile());
    }


    @GetMapping("/user/{userName}")
    public ResponseEntity<ProfileResponse> getProfileByUserName(@PathVariable String userName) {
        return ResponseEntity.ok(service.getProfileByUserName(userName));
    }


    // GET /profiles/search
    @GetMapping("/search")
    public ResponseEntity<Page<ProfileResponse>> search(
            @RequestParam String q,
            @PageableDefault(12)
            Pageable pageable) {
        return ResponseEntity.ok(service.searchProfiles(q, pageable));
    }

    // PUT
    @PutMapping("me")
    public ResponseEntity<ProfileResponse> uptadeMyProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(service.updateMyProfile(request));
    }

}
