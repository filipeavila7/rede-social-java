package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
        SELECT c FROM Conversation c
        WHERE c.userA.id = :userId OR c.userB.id = :userId
        ORDER BY c.createdAt DESC
    """)
    List<Conversation> findAllByUserId(Long userId);

    @Query("""
        SELECT c FROM Conversation c
        WHERE (c.userA.id = :a AND c.userB.id = :b)
           OR (c.userA.id = :b AND c.userB.id = :a)
    """)
    java.util.Optional<Conversation> findBetweenUsers(Long a, Long b);
}
