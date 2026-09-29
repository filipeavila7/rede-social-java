package com.example.demo.likeStory.repository;

import com.example.demo.likeStory.entity.LikeStory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeStoryRepository extends JpaRepository<LikeStory, Long> {
    Optional<LikeStory> findByStoryIdAndUserId(Long storyId, Long userId);
}
