
package com.example.demo.profile.dto;


import com.example.demo.followRequest.entity.FollowRequestStatus;

public record ProfileResponse(
        Long userId,
        String name,
        String bio,
        String imageUrlProfile,
        String messageStatus,
        String userName,
        long followCount,
        long followerCount,
        long postCount,
        boolean amIfollowing,
        FollowRequestStatus followRequestStatus
) {}
