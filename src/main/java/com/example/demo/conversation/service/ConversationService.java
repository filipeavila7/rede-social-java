package com.example.demo.conversation.service;


import com.example.demo.conversation.mapper.ConversationMapper;
import com.example.demo.helpers.GlobalHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.conversation.dto.ConversationResponse;
import com.example.demo.conversation.entity.Conversation;
import com.example.demo.user.entity.User;
import com.example.demo.conversation.repository.ConversationRepository;


@Service
@RequiredArgsConstructor
public class ConversationService {


    private final ConversationRepository conversationRepository;
    private final GlobalHelperService globalHelperService;
    private final ConversationMapper conversationMapper;


    // retorna todas as conversas do usuario logado
    public Page<ConversationResponse> getMyConversations(Pageable pageable) {
        User loggedUser = globalHelperService.getLoggedUser();

        return conversationRepository.findAllByUserIdOrderByLastMessage(
                loggedUser.getId(), pageable)
                .map(c -> conversationMapper.toConversationResponse(c, loggedUser));
    }


    // criar conversa caso ela ainda não exista
    public ConversationResponse openConversation(Long otherUserId) {
        // pegar o user logado
        User loggedUser = globalHelperService.getLoggedUser();

        // não permitir que o usuario crie uma conversa consigo mesmo
        if (loggedUser.getId().equals(otherUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode abrir conversa consigo mesmo");
        }

        // encontra o outro usuario
        User other = globalHelperService.findUserById(otherUserId);

        // verifica se ja existe uma conversation entre eles, se não existir, cria uma
        Conversation conversation = conversationRepository
                .findBetweenUsers(loggedUser.getId(), other.getId())
                .orElseGet(() -> conversationRepository.save(new Conversation(loggedUser, other)));


        return conversationMapper.toConversationResponse(conversation, loggedUser);
    }

}
