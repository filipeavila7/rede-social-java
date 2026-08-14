package com.example.demo.feed.interaction.repository;

import com.example.demo.feed.interaction.entity.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
}
