package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.dto.ConversationUpdateResponse;
import com.example.demo.dto.MessageResponse;
import com.example.demo.dto.NotificationRealtimeResponse;
import com.example.demo.dto.UnreadCountResponse;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.Message;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    public MessageService(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            WebSocketService webSocketService
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.webSocketService = webSocketService;
    }

    // =========================
    // AUTH USER
    // =========================
    private User getLoggedUser() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não encontrado"
            );
        }

        return user;
    }

    private boolean belongsToConversation(Conversation c, User u) {
        return u.getId().equals(c.getUserA().getId()) ||
                u.getId().equals(c.getUserB().getId());
    }

    // =========================
    // SEND MESSAGE
    // =========================
    public MessageResponse sendMessage(Long receiverId, String content) {

        if (content == null || content.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Conteúdo vazio"
            );
        }

        User sender = getLoggedUser();

        if (sender.getId().equals(receiverId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Você não pode enviar mensagem para si mesmo"
            );
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));

        Conversation conversation = conversationRepository
                .findBetweenUsers(sender.getId(), receiver.getId())
                .orElseGet(() -> conversationRepository.save(
                        new Conversation(sender, receiver)
                ));

        if (!belongsToConversation(conversation, sender)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pertence a essa conversa"
            );
        }

        Message message = new Message(conversation, sender, content);
        Message saved = messageRepository.save(message);

        MessageResponse response = toResponse(saved);

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
        NotificationRealtimeResponse notification =
                new NotificationRealtimeResponse(
                        "MESSAGE",
                        sender.getId(),
                        sender.getNome(),
                        sender.getUserName(),
                        sender.getProfile() != null
                                ? sender.getProfile().getImageUrlProfile()
                                : null,
                        null,
                        conversation.getId(),
                        saved.getId(),
                        content,
                        LocalDateTime.now()
                );

        webSocketService.sendNotificationToUser(
                receiver.getId(),
                notification
        );

        // =========================
        // UPDATE DE CONVERSA (NOVO)
        // =========================
        ConversationUpdateResponse convUpdate =
                new ConversationUpdateResponse(
                        conversation.getId(),
                        saved.getContent(),
                        saved.getCreatedAt(),
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

            NotificationRealtimeResponse readNotification =
                    new NotificationRealtimeResponse(
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