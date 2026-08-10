
package com.example.demo.profile.dto;


public record ProfileResponse(
        Long userId,
        String name,
        String bio,
        String imageUrlProfile,
        String messageStatus,
        String userName
) {}
