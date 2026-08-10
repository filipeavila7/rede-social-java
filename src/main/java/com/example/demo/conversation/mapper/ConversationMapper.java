package com.example.demo.conversation.mapper;

import com.example.demo.conversation.dto.ConversationResponse;
import com.example.demo.conversation.entity.Conversation;
import com.example.demo.entity.Message;
import com.example.demo.repository.MessageRepository;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationMapper {
    private final MessageRepository messageRepository;


    public ConversationResponse toResponse(
            Conversation conversation,
            User loggedUser
    ) {
        User otherUser = conversation.getUserA().getId().equals(loggedUser.getId())
                ? conversation.getUserB()
                : conversation.getUserA();

        Message last = messageRepository
                .findFirstByConversationIdOrderByCreatedAtDesc(conversation.getId())
                .orElse(null);

        String lastMsg = last != null ? last.getContent() : null;
        String lastAt = last != null ? last.getCreatedAt().toString() : null;

        return new ConversationResponse(
                conversation.getId(),
                otherUser.getId(),
                otherUser.getName(),
                otherUser.getProfile().getImageUrlProfile(),
                lastMsg,
                lastAt
        );
    }
}
