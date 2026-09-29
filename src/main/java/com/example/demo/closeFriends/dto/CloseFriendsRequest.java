package com.example.demo.closeFriends.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CloseFriendsRequest(
        @NotNull
        Set<Long> userIds
) {
}
