package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Message;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId); // listar messagens de uma conversa
    Page<Message> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);
    Optional<Message> findFirstByConversationIdOrderByCreatedAtDesc(Long conversationId); // lista ultima messagem enviada

    @Query("""
    SELECT m.conversation.id, COUNT(m)
    FROM Message m
    WHERE m.conversation.id IN :conversationIds
      AND m.readAt IS NULL
      AND m.sender.id <> :userId
    GROUP BY m.conversation.id
""")
    List<Object[]> countUnreadByConversations(
            List<Long> conversationIds,
            Long userId
    );
}
