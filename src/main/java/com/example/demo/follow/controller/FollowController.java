package com.example.demo.follow.controller;

import com.example.demo.dto.FollowingProfileResponse;
import com.example.demo.follow.dto.FollowResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.demo.follow.service.FollowService;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/follow")
public class FollowController {
    public final FollowService service;

    public FollowController(FollowService service) {
        this.service = service;
    }


    @PostMapping("/{userId}")
    public ResponseEntity<FollowResponse> followUser(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.followUser(userId));
    }


    @DeleteMapping("/unfollow/{userId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId) {
        service.unfollowUser(userId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/remove-follower/{followerId}")
    public ResponseEntity<Void> removeFollower(@PathVariable Long followerId) {
        service.removeFollower(followerId);
        return ResponseEntity.noContent().build();
    }

    // contagem de seguidores e seguindo de outro user
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Long> countFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(service.countFollowers(userId));
    }

    // GET /users/{userId}/following/count
    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Long> countFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(service.countFollowing(userId));

    }



    @GetMapping("/{userId}/following")
    public ResponseEntity<Page<FollowingProfileResponse>> getFollowing(
            @PathVariable Long userId,
            @PageableDefault(size = 12)
            Pageable pageable)
    {
        return ResponseEntity.ok(service.getFollowing(userId, pageable));
    }

    @GetMapping("/{id}/followingStatus")
    public boolean amIFollowing(@PathVariable Long id) {
        return service.amIFollowing(id);
    }

    // GET /users/{userId}/followers
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<FollowingProfileResponse>> getFollowers(
            @PathVariable Long userId,
            @PageableDefault(size = 12)
            Pageable pageable) {
        return ResponseEntity.ok(service.getFollowers(userId, pageable));
    }



    @GetMapping("/me/following")
    public ResponseEntity<Page<FollowingProfileResponse>> getMyFollowing(
            @PageableDefault(size = 12)
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.getMyFollowing(pageable));
    }

    // GET /users/me/followers
    @GetMapping("/me/followers")
    public ResponseEntity<Page<FollowingProfileResponse>> getMyFollowers(
            @PageableDefault(size = 12)
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.getMyFollowers(pageable));
    }

}
