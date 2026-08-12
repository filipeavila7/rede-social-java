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
import org.springframework.data.domain.Page;
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
    // TODO - POSSIVEL METODO REDUNDATE - a repository de achar conversation ja garante que pertence aquele usuario
    private boolean belongsToConversation(Conversation c, User u) {
        return u.getId().equals(c.getUserA().getId()) ||
                u.getId().equals(c.getUserB().getId());
    }

    // enviar mensagem
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

        // salva mensagem e conversation
        MessageResponse response = messageMapper.toMessageResponse(messageRepository.save(message));
        conversationRepository.save(conversation);

       // manda mensagem em tempo real
        webSocketService.sendMessageToConversation(
                conversation.getId(),
                response
        );

        // cria notificação
        notificationService.createChatNotification(
                sender, receiver, NotificationType.MESSAGE, request.textMessage(), conversation.getId(),
                message.getId());


        // update de conversa
        ConversationUpdateResponse convUpdate =
                new ConversationUpdateResponse(
                        conversation.getId(),
                        message.getContent(),
                        message.getCreatedAt(),
                        sender.getId()
                );

        // atualiza pros 2 usuarios na conversa
        webSocketService.sendConversationUpdate(sender.getId(), convUpdate);
        webSocketService.sendConversationUpdate(receiver.getId(), convUpdate);

        return response;
    }

    // ler mensagens
    @Transactional
    public void markConversationAsRead(Long conversationId) {

        User loggedUser = globalHelperService.getLoggedUser();

        Conversation conversation = globalHelperService.getConversationById(conversationId);

        if (!belongsToConversation(conversation, loggedUser)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pertence a essa conversa"
            );
        }

        Long otherUserId;

        if (conversation.getUserA().getId().equals(loggedUser.getId())) {
            otherUserId = conversation.getUserB().getId();
        } else {
            otherUserId = conversation.getUserA().getId();
        }

        List<Message> messages =
                messageRepository.findByConversationIdAndSenderIdAndReadAtIsNull(
                        conversationId,
                        otherUserId
                );

        if (messages.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        messages.forEach(message -> message.setReadAt(now));

        messageRepository.saveAll(messages);

        notificationService.sendMessageReadNotification(
                loggedUser,
                otherUserId,
                conversationId,
                now,
                NotificationType.READ
        );
    }

    // =========================
    // GET MESSAGES
    // =========================
    public Page<MessageResponse> getMessages(
            Long conversationId,
            Pageable pageable
    ) {

        User loggedUser = globalHelperService.getLoggedUser();

        Conversation conversation = globalHelperService.getConversationById(conversationId);

        if (!belongsToConversation(conversation, loggedUser)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pertence a essa conversa"
            );
        }

        markConversationAsRead(conversationId);

       return messageRepository.findByConversationIdOrderByCreatedAtDesc(
                        conversationId,
                        pageable
                )
                .map(messageMapper::toMessageResponse);

    }

    // =========================
    // UNREAD COUNT (BADGE)
    // =========================
    // =========================
// UNREAD COUNT (BADGE)
// =========================
    public List<UnreadCountResponse> getUnreadConversations() {

        User me = globalHelperService.getLoggedUser();

        List<Conversation> conversations =
                conversationRepository.findAllByUserId(me.getId());

        if (conversations.isEmpty()) {
            return List.of();
        }

        List<Long> conversationIds = conversations.stream()
                .map(Conversation::getId)
                .toList();

        List<Object[]> result =
                messageRepository.countUnreadByConversations(
                        conversationIds,
                        me.getId()
                );

        Map<Long, Long> countMap = result.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        return conversations.stream()
                .map(conversation -> new UnreadCountResponse(
                        conversation.getId(),
                        countMap.getOrDefault(conversation.getId(), 0L)
                ))
                .toList();
    }

}