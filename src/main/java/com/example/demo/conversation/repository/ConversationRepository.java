package com.example.demo.conversation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.conversation.entity.Conversation;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
        SELECT c FROM Conversation c
        WHERE c.userA.id = :userId OR c.userB.id = :userId
        ORDER BY c.createdAt DESC
    """)
    List<Conversation> findAllByUserId(Long userId);

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

    @Query("""
    SELECT m.conversation.id, COUNT(m)
    FROM Message m
    WHERE m.conversation.id IN :conversationIds
      AND m.sender.id <> :userId
      AND m.readAt IS NULL
    GROUP BY m.conversation.id
""")
    List<Object[]> countUnreadByConversations(
            @Param("conversationIds") List<Long> conversationIds,
            @Param("userId") Long userId
    );
}
