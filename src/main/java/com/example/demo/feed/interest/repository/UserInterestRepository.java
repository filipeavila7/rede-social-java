package com.example.demo.feed.interest.repository;

import com.example.demo.feed.interest.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    Optional<UserInterest> findByUserIdAndTagId(Long userId, Long tagId);
}
