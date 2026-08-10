package com.example.demo.conversation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.conversation.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
        SELECT c FROM Conversation c
        WHERE c.userA.id = :userId OR c.userB.id = :userId
        ORDER BY c.createdAt DESC
    """)
    Page<Conversation> findAllByUserId(Long userId, Pageable pageable);

    @Query("""
        SELECT c FROM Conversation c
        LEFT JOIN Message m ON m.conversation.id = c.id
        WHERE c.userA.id = :userId OR c.userB.id = :userId
        GROUP BY c.id
        ORDER BY MAX(m.createdAt) DESC
    """)
    Page<Conversation> findAllByUserIdOrderByLastMessage(Long userId, Pageable pageable);

    @Query("""
        SELECT c FROM Conversation c
        WHERE (c.userA.id = :a AND c.userB.id = :b)
           OR (c.userA.id = :b AND c.userB.id = :a)
    """)
    java.util.Optional<Conversation> findBetweenUsers(Long a, Long b);
}
