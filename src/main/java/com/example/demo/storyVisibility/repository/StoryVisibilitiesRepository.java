package com.example.demo.storyVisibility.repository;


import com.example.demo.storyVisibility.entity.StoryVisibilities;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryVisibilitiesRepository extends JpaRepository<StoryVisibilities, Long> {
    boolean existsByStoryIdAndUserId(Long storyId, Long userId);

    Page<StoryVisibilities> findByStoryId(
            Long storyId,
            Pageable pageable
    );
}
