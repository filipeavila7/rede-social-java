package com.example.demo.message.mapper;

import com.example.demo.message.dto.MessageResponse;
import com.example.demo.message.entity.Message;
import com.example.demo.util.FileUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final FileUrlUtils fileUrlUtils;

    public MessageResponse toMessageResponse(Message m){
        return new MessageResponse(
                m.getId(),
                m.getConversation().getId(),
                m.getSender().getId(),
                m.getSender().getName(),
                fileUrlUtils.toPublicUrl(m.getSender().getProfile().getImageUrlProfile()),
                m.getContent(),
                m.getCreatedAt().toString(),
                m.getReadAt().toString()
        );
    }
}
