package com.example.demo.conversation.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entity.Message;
import com.example.demo.user.entity.User;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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


    @ManyToOne
    @JoinColumn(name = "user_a_id", nullable = false)
    private User userA;


    @ManyToOne
    @JoinColumn(name = "user_b_id", nullable = false)
    private User userB;


    @Column(nullable = false)
    private LocalDateTime createdAt;


    @Column
    private String lastMessage;

    @Column
    private LocalDateTime lastMessageAt;







    

    

}
