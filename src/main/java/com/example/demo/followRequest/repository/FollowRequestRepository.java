package com.example.demo.followRequest.repository;

import com.example.demo.followRequest.entity.FollowRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    Optional<FollowRequest> findByRequesterIdAndTargetId(
            Long requesterId,
            Long targetId
    );

    void deleteByRequesterIdAndTargetId(Long requesterId, Long targetId);
}
