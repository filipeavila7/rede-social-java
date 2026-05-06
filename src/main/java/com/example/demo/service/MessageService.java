package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dto.NotificationResponse;
import com.example.demo.dto.ReadNotificationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.MessageResponse;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.Message;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    public MessageService(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository,
            UserRepository userRepository, WebSocketService webSocketService
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.webSocketService = webSocketService;
    }

    private User getLoggedUser() {
        String email = (String) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }
        return user;
    }

    // ver se o usuario é o msm da conversa
    private boolean belongsToConversation(Conversation c, User u) {
        return u.getId().equals(c.getUserA().getId()) ||
               u.getId().equals(c.getUserB().getId());
    }

    // Enviar mensagem (cria conversa se nao existir)
    // enviar mensagem para usuario passando o id e o content
    public MessageResponse sendMessage(Long receiverId, String content) {
        if (content == null || content.trim().isEmpty() ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conteúdo vazio");
        }
        User sender = getLoggedUser(); // define que quem mandou é o usuario logado


        // caso o id do logado seja igual ao id passado para enviar a msg
        if (sender.getId().equals(receiverId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode enviar mensagem para si mesmo");
        }
        // pegar usuario do banco pelo id passado
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // encontrar a conversa no banco, caso ainda não exista, ele cria com quem mandou e quem ta recebendo
        Conversation conversation = conversationRepository
            .findBetweenUsers(sender.getId(), receiver.getId())
            .orElseGet(() -> conversationRepository.save(new Conversation(sender, receiver))); // se não tiver chat, cria apos a msg

        // segurança extra (garante que só participantes enviem)
        if (!belongsToConversation(conversation, sender)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pertence a essa conversa");
        }

        // criar nova mensagem e salvar no banco
        Message message = new Message(conversation, sender, content);
        Message saved = messageRepository.save(message);

        MessageResponse response = toResponse(saved);

        NotificationResponse notification = new NotificationResponse(
                "MESSAGE",
                conversation.getId(),
                sender.getId(),
                sender.getNome(),
                sender.getProfile() != null ? sender.getProfile().getImageUrlProfile() : null,
                content
        );

        webSocketService.sendMessageToConversation(conversation.getId(), response);
        webSocketService.sendNotificationToUser(receiver.getId(), notification);

        return response;


    }

    public List<MessageResponse> getMessages(Long conversationId) {
        User me = getLoggedUser(); // usuario logado que abriu o chat

        // busca a conversa no banco
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversa não encontrada"));

        // garante que ele pertence a essa conversa
        if (!belongsToConversation(conversation, me)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pertence a essa conversa");
        }

        // pega todas as mensagens da conversa em ordem
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        // lista para armazenar somente as mensagens que foram lidas AGORA
        List<Message> readNowMessages = new ArrayList<>();

        // percorre todas as mensagens
        for (Message msg : messages) {

            // verifica se a mensagem foi enviada pelo OUTRO usuário
            boolean isFromOtherUser = !msg.getSender().getId().equals(me.getId());

            // se veio do outro e ainda nao foi lida
            if (isFromOtherUser && msg.getReadAt() == null) {

                // marca horario de leitura
                msg.setReadAt(LocalDateTime.now());

                // adiciona na lista das que acabaram de ser lidas
                readNowMessages.add(msg);
            }
        }

        // se realmente houve mensagens lidas agora
        if (!readNowMessages.isEmpty()) {

            // salva somente as alteradas
            messageRepository.saveAll(readNowMessages);

            // pega os ids dessas mensagens para enviar ao remetente
            List<Long> messageIds = readNowMessages.stream()
                    .map(Message::getId)
                    .toList();

            // como todas essas mensagens vieram do outro usuário,
            // podemos pegar o senderId da primeira
            Long senderId = readNowMessages.get(0).getSender().getId();

            // monta dto de notificação de leitura
            ReadNotificationResponse readNotification = new ReadNotificationResponse(
                    "READ",
                    messageIds
            );

            // envia websocket realtime para quem mandou as mensagens
            webSocketService.sendReadStatusToUser(senderId, readNotification);
        }

        // retorna mensagens normalmente para quem abriu o chat
        return messages.stream()
                .map(this::toResponse)
                .toList();
    }

    // Converte Message para DTO com foto do perfil do remetente
    private MessageResponse toResponse(Message message) {
        User sender = message.getSender();
        Profile profile = sender.getProfile();
        String photo = profile != null ? profile.getImageUrlProfile() : null;
        String createdAt = message.getCreatedAt() != null ? message.getCreatedAt().toString() : null;
        String readAt = message.getReadAt() != null ? message.getReadAt().toString() : null;

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
