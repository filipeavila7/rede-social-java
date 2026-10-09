package com.example.demo.message.entity;

import java.time.LocalDateTime;

import com.example.demo.conversation.entity.Conversation;
import com.example.demo.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "messages")

public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // chatId
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;


    // quem envia
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    
    public Message() {
    }

    public Message(Conversation conversation, User sender, String content) {
        this.conversation = conversation;
        this.sender = sender;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }


}
