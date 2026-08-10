package com.example.demo.conversation.mapper;

import com.example.demo.conversation.dto.ConversationResponse;
import com.example.demo.conversation.entity.Conversation;
import com.example.demo.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ConversationMapper {
    public ConversationResponse toConversationResponse(
            Conversation conversation,
            User loggedUser
    ) {
        User otherUser = conversation.getUserA().getId().equals(loggedUser.getId())
                ? conversation.getUserB()
                : conversation.getUserA();

        return new ConversationResponse(
                conversation.getId(),
                otherUser.getId(),
                otherUser.getName(),
                otherUser.getProfile().getImageUrlProfile(),
                conversation.getLastMessage(),
                conversation.getLastMessageAt()
        );
    }
}
