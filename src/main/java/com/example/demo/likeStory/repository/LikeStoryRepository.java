package com.example.demo.likeStory.repository;

import com.example.demo.likeStory.entity.LikeStory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeStoryRepository extends JpaRepository<LikeStory, Long> {
}
