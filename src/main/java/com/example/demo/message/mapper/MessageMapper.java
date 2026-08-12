package com.example.demo.message.mapper;

import com.example.demo.message.dto.MessageResponse;
import com.example.demo.message.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageResponse toMessageResponse(Message m){
        return new MessageResponse(
                m.getId(),
                m.getConversation().getId(),
                m.getSender().getId(),
                m.getSender().getName(),
                m.getSender().getProfile().getImageUrlProfile(),
                m.getContent(),
                m.getCreatedAt().toString(),
                m.getReadAt().toString()
        );
    }
}
