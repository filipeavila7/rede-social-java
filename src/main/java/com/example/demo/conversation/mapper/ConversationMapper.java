package com.example.demo.conversation.mapper;

import com.example.demo.conversation.dto.ConversationResponse;
import com.example.demo.conversation.entity.Conversation;
import com.example.demo.user.entity.User;
import com.example.demo.util.FileUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConversationMapper {
    private final FileUrlUtils fileUrlUtils;

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
                fileUrlUtils.toPublicUrl(otherUser.getProfile().getImageUrlProfile()),
                conversation.getLastMessage(),
                conversation.getLastMessageAt()
        );
    }
}
