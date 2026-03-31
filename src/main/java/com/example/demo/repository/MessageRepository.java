package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId); // listar messagens de uma conversa

    Optional<Message> findFirstByConversationIdOrderByCreatedAtDesc(Long conversationId); // lista ultima messagem enviada
}
