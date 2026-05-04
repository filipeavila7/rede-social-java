package com.example.demo.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.FollowingProfileResponse;
import com.example.demo.entity.Post;
import com.example.demo.dto.ProfileResponse;
import com.example.demo.entity.Profile;
import com.example.demo.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/profiles")
public class ProfileController {
    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    // GET
    // /profiles/me perfil do usuario logado
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile() {
        // retronar os dados da profile do usuario em json
        return ResponseEntity.ok(service.getMyProfile());
    }

    // GET /profiles/user?userName
    @GetMapping("/user")
    public ResponseEntity<ProfileResponse> getProfileByUserName(@RequestParam String userName) {
        return ResponseEntity.ok(service.getProfileByUserName(userName));
    }

    // GET /profiles/following
    @GetMapping("/following")
    public ResponseEntity<List<FollowingProfileResponse>> getFollowingProfiles() {
        return ResponseEntity.ok(service.getFollowingProfiles());
    }

    // GET /profiles/followers
    @GetMapping("/followers")
    public ResponseEntity<List<FollowingProfileResponse>> getFollowersProfiles() {
        return ResponseEntity.ok(service.getFollowersProfiles());
    }

    // GET /profiles/search
    @GetMapping("/search")
    public ResponseEntity<List<ProfileResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(service.searchProfiles(q));
    }

    // PUT
    @PutMapping("me")
    public ResponseEntity<Profile> uptadeMyProfile(@Valid @RequestBody Profile profile) {
        Profile profileUpdate = service.updateMyProfile(profile);

        return ResponseEntity.ok(profileUpdate);
    }

}
