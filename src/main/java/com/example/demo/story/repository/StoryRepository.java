package com.example.demo.story.repository;

import com.example.demo.story.entity.Story;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface StoryRepository extends JpaRepository<Story, Long> {


    Page<Story> findByUserUserNameAndExpiresAtAfterOrderByCreatedAtAsc(
            String userName,
            LocalDateTime now,
            Pageable pageable
    );
}
