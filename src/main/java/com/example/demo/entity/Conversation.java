package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "conversations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_a_id", "user_b_id"}) // evitar 2 contatos com os msm users duplicado
)
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToMany(mappedBy = "conversation")
    @JsonIgnore
    private List<Message> messages;



    // relacionar 2 usuários diferentes em uma conversa, como se fosse o contato de 2 users
    // Usuáriio A
    @ManyToOne
    @JoinColumn(name = "user_a_id", nullable = false)
    private User userA;

    // Usuário B
    @ManyToOne
    @JoinColumn(name = "user_b_id", nullable = false)
    private User userB;


    @Column(nullable = false)
    private LocalDateTime createdAt;


    public Conversation() {
    }


    public Conversation(User userA, User userB) {
        this.userA = userA;
        this.userB = userB;
        this.createdAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public User getUserA() {
        return userA;
    }


    public void setUserA(User userA) {
        this.userA = userA;
    }


    public User getUserB() {
        return userB;
    }


    public void setUserB(User userB) {
        this.userB = userB;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }




    

    

}
