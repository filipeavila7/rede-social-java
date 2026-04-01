package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.ConversationResponse;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ConversationService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    

    


    public ConversationService(MessageRepository messageRepository, ConversationRepository conversationRepository,
            UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }


    // retorna todas as conversas do usuario logado
    public List<Conversation> getMyConversations() {
        String email = (String) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        User me = userRepository.findByEmail(email);
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        return conversationRepository.findAllByUserIdOrderByLastMessage(me.getId());
    }


    public List<ConversationResponse> getMyContacts() {
    // 1) pegar usuario logado
    String email = (String) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

    User me = userRepository.findByEmail(email);
    if (me == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
    }

    // 2) pegar todas as conversas dele
    List<Conversation> conversations = conversationRepository.findAllByUserIdOrderByLastMessage(me.getId());

    // 3) montar lista de contatos
    List<ConversationResponse> contatos = new ArrayList<>();

    for (Conversation c : conversations) {
        // descobrir o outro usuario
        User other; // outro usuario
        if (c.getUserA().getId().equals(me.getId())) { // se o id do user A for igual ao do logado, então o outro é o b
            other = c.getUserB();
        } else {
            other = c.getUserA(); // caso o id do user logado não for igual o do user A, ent o outro é o B
        }

        // pegar foto do perfil do outro usuario
        String foto = null;
        if (other.getProfile() != null) {
            foto = other.getProfile().getImageUrlProfile();
        }

        // buscar ultima mensagem da conversa
        Message last = messageRepository
            .findFirstByConversationIdOrderByCreatedAtDesc(c.getId())
            .orElse(null);

        String lastMsg = last != null ? last.getContent() : null;
        String lastAt = last != null ? last.getCreatedAt().toString() : null;

        // criar DTO
        contatos.add(new ConversationResponse(
            c.getId(),
            other.getId(),
            other.getNome(),
            foto,
            lastMsg,
            lastAt
        ));
    }

    return contatos;
}

}
