package com.example.demo.feed.interest.repository;

import com.example.demo.feed.interest.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    List<UserInterest> findByUserIdAndTagIdIn(Long userId, List<Long> tagIds);
}
