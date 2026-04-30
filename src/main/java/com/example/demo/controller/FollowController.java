package com.example.demo.controller;

import com.example.demo.dto.FollowingProfileResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Follow;
import com.example.demo.entity.User;
import com.example.demo.service.FollowService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/users")
public class FollowController {
    public final FollowService service;

    public FollowController(FollowService service) {
        this.service = service;
    }

    // POST /users/{userId}/follow
    @PostMapping("/{userId}/follow")
    public ResponseEntity<Follow> followUser(@PathVariable Long userId) {
        Follow follow = service.followUser(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(follow);
    }

    // DELETE /users/{userId}/follow
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId) {
        service.unfollowUser(userId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /users/followers/{followerId}
    @DeleteMapping("/followers/{followerId}")
    public ResponseEntity<Void> removeFollower(@PathVariable Long followerId) {
        service.removeFollower(followerId);
        return ResponseEntity.noContent().build();
    }

    // contagem de seguidores e seguindo de outro user
    // GET /users/{userId}/followers/count
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Long> countFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(service.countFollowers(userId));
    }

    // GET /users/{userId}/following/count
    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Long> countFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(service.countFollowing(userId));

    }


    // seguidores e seguindo de outros users
    // GET /users/{userId}/following
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<FollowingProfileResponse>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getFollowing(userId));
    }

    // GET /users/{userId}/followers
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<FollowingProfileResponse>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getFollowers(userId));
    }


    // seguidores e seguindo do user logado

    // GET /users/me/following
    @GetMapping("/me/following")
    public ResponseEntity<List<User>> getMyFollowing() {
        return ResponseEntity.ok(service.getMyFollowing());
    }

    // GET /users/me/followers
    @GetMapping("/me/followers")
    public ResponseEntity<List<User>> getMyFollowers() {
        return ResponseEntity.ok(service.getMyFollowers());
    }

}
