package com.example.demo.profile.controller;

import java.util.List;

import com.example.demo.profile.dto.ProfileUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.follow.dto.FollowingProfileResponse;
import com.example.demo.profile.dto.ProfileResponse;
import com.example.demo.profile.entity.Profile;
import com.example.demo.profile.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/profiles")
public class ProfileController {
    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }


    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile() {
        // retronar os dados da profile do usuario em json
        return ResponseEntity.ok(service.getMyProfile());
    }


    @GetMapping("/user")
    public ResponseEntity<ProfileResponse> getProfileByUserName(@RequestParam String userName) {
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
