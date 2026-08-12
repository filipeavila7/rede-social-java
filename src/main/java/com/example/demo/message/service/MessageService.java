package com.example.demo.message.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.message.dto.ConversationUpdateResponse;
import com.example.demo.message.dto.MessageRequest;
import com.example.demo.message.dto.MessageResponse;
import com.example.demo.message.mapper.MessageMapper;
import com.example.demo.notification.dto.NotificationPostResponse;
import com.example.demo.message.dto.UnreadCountResponse;
import com.example.demo.conversation.entity.Conversation;
import com.example.demo.message.entity.Message;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.notification.service.NotificationService;
import com.example.demo.profile.entity.Profile;
import com.example.demo.websocket.WebSocketService;
import com.example.demo.user.entity.User;
import com.example.demo.conversation.repository.ConversationRepository;
import com.example.demo.message.repository.MessageRepository;
import com.example.demo.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MessageService {

    // TODO - a entidade de conversation possui campos de ultima mensagem e a data da ultima mensagem
    // TODO - no post de mensagens criar o set

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final NotificationService notificationService;
    private final WebSocketService webSocketService;
    private final GlobalHelperService globalHelperService;
    private final MessageMapper messageMapper;



    // verifica se o id do user é igual a do user a ou b na conversation
    private boolean belongsToConversation(Conversation c, User u) {
        return u.getId().equals(c.getUserA().getId()) ||
                u.getId().equals(c.getUserB().getId());
    }

    // =========================
    // SEND MESSAGE
    // =========================
    @Transactional
    public MessageResponse sendMessage(Long receiverId, MessageRequest request) {
        // quem esta enviando é o user logado
        User sender = globalHelperService.getLoggedUser();

        // procura o usuario que ta recebendo
        User receiver = globalHelperService.findUserById(receiverId);

        // se eles forem iguais lança exceção
        if (sender.getId().equals(receiverId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Você não pode enviar mensagem para si mesmo"
            );
        }

        // acha coversation ou cria caso ainda não exista
        Conversation conversation = globalHelperService.findConversationOrNew(sender, receiver);

        // caso o usuario não pertença aquela conversa
        if (!belongsToConversation(conversation, sender)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pertence a essa conversa"
            );
        }

        // cria a mensagem
        Message message = new Message();
        message.setContent(request.textMessage());
        message.setConversation(conversation);
        message.setSender(sender);

        // ultima mensagem e hora da ultima mensagem
        conversation.setLastMessage(request.textMessage());
        conversation.setLastMessageAt(message.getCreatedAt());

        // saçva mensagem e conversation
        MessageResponse response = messageMapper.toMessageResponse(messageRepository.save(message));
        conversationRepository.save(conversation);

        // =========================
        // CHAT REALTIME (MENSAGEM)
        // =========================
        webSocketService.sendMessageToConversation(
                conversation.getId(),
                response
        );

        // =========================
        // NOTIFICAÇÃO GLOBAL
        // =========================
        notificationService.createChatNotification(
                sender, receiver, NotificationType.MESSAGE, request.textMessage(), conversation.getId(),
                message.getId());


        // =========================
        // UPDATE DE CONVERSA (NOVO)
        // =========================
        ConversationUpdateResponse convUpdate =
                new ConversationUpdateResponse(
                        conversation.getId(),
                        message.getContent(),
                        message.getCreatedAt(),
                        sender.getId()
                );

        webSocketService.sendConversationUpdate(sender.getId(), convUpdate);
        webSocketService.sendConversationUpdate(receiver.getId(), convUpdate);

        return response;
    }

    // =========================
    // MARK AS READ
    // =========================
    public void markConversationAsRead(Long conversationId) {

        User loggedUser = globalHelperService.getLoggedUser();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conversa não encontrada"
                ));

        if (!belongsToConversation(conversation, loggedUser)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pertence a essa conversa"
            );
        }

        List<Message> messages =
                messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        List<Message> updated = new ArrayList<>();

        for (Message msg : messages) {

            boolean isOtherUser = !msg.getSender().getId().equals(me.getId());

            if (isOtherUser && msg.getReadAt() == null) {
                msg.setReadAt(LocalDateTime.now());
                updated.add(msg);
            }
        }

        if (!updated.isEmpty()) {

            messageRepository.saveAll(updated);

            Long senderId = updated.get(0).getSender().getId();

            NotificationPostResponse readNotification =
                    new NotificationPostResponse(
                            "READ",
                            me.getId(),
                            me.getNome(),
                            me.getUserName(),
                            me.getProfile() != null
                                    ? me.getProfile().getImageUrlProfile()
                                    : null,
                            null,
                            conversationId,
                            null,
                            null,
                            LocalDateTime.now()
                    );

            webSocketService.sendNotificationToUser(
                    senderId,
                    readNotification
            );
        }
    }

    // =========================
    // GET MESSAGES
    // =========================

    public List<MessageResponse> getMessages(
            Long conversationId,
            int page,
            int size
    ) {

        User me = getLoggedUser();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conversa não encontrada"
                ));

        if (!belongsToConversation(conversation, me)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pertence a essa conversa"
            );
        }

        markConversationAsRead(conversationId);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        List<MessageResponse> messages = new ArrayList<>(
                messageRepository
                        .findByConversationIdOrderByCreatedAtDesc(
                                conversationId,
                                pageable
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );

        // inverte para ficar em ordem correta no chat
        Collections.reverse(messages);

        return messages;
    }

    // =========================
    // UNREAD COUNT (BADGE)
    // =========================
    public List<UnreadCountResponse> getUnreadConversations() {

        User me = getLoggedUser();

        List<Conversation> conversations =
                conversationRepository.findAllByUserId(me.getId());

        if (conversations.isEmpty()) {
            return List.of();
        }

        List<Long> ids = conversations.stream()
                .map(Conversation::getId)
                .toList();

        List<Object[]> result =
                messageRepository.countUnreadByConversations(ids, me.getId());

        Map<Long, Long> countMap = result.stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1],
                        Long::sum
                ));

        return conversations.stream()
                .map(c -> new UnreadCountResponse(
                        c.getId(),
                        countMap.getOrDefault(c.getId(), 0L)
                ))
                .toList();
    }

    // =========================
    // MAPPER
    // =========================
    private MessageResponse toResponse(Message message) {

        User sender = message.getSender();
        Profile profile = sender.getProfile();

        String photo = profile != null
                ? profile.getImageUrlProfile()
                : null;

        String createdAt = message.getCreatedAt() != null
                ? message.getCreatedAt().toString()
                : null;

        String readAt = message.getReadAt() != null
                ? message.getReadAt().toString()
                : null;

        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                sender.getId(),
                sender.getNome(),
                photo,
                message.getContent(),
                createdAt,
                readAt
        );
    }
}