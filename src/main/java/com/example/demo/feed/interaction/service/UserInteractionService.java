package com.example.demo.feed.interaction.service;

import com.example.demo.feed.interaction.entity.InteractionType;
import com.example.demo.feed.interaction.entity.UserInteraction;
import com.example.demo.feed.interaction.repository.UserInteractionRepository;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.post.entity.Post;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInteractionService {
    private final UserInteractionRepository userInteractionRepository;
    private final GlobalHelperService globalHelperService;


    // criar intereaçao de view
    public void registerView(Long postId, int durationSeconds){
        // caso a duração for muito curta, não salva
        if (durationSeconds < 10 ){
            return;
        }
        // seguranã para caso o front mande um numero muito grande
        if (durationSeconds > 300){
            durationSeconds = 300;
        }

        // encontra user logado e post
        User loggedUser = globalHelperService.getLoggedUser();
        Post post = globalHelperService.findPostById(postId);

        UserInteraction interaction = new UserInteraction();

        // cria interaçao
        interaction.setUser(loggedUser);
        interaction.setPost(post);
        interaction.setType(InteractionType.VIEW);
        interaction.setDurationSeconds(durationSeconds);

        userInteractionRepository.save(interaction);
    }

    // salvar interação de like
    public void registerLike(Long postId){
        // encontra user logado e post
        User loggedUser = globalHelperService.getLoggedUser();
        Post post = globalHelperService.findPostById(postId);

        UserInteraction interaction = new UserInteraction();

        // cria interaçao
        interaction.setUser(loggedUser);
        interaction.setPost(post);
        interaction.setType(InteractionType.LIKE);


        userInteractionRepository.save(interaction);
    }


    // salvar interação de comentário
    public void registerComment(Long postId){
        // encontra user logado e post
        User loggedUser = globalHelperService.getLoggedUser();
        Post post = globalHelperService.findPostById(postId);

        UserInteraction interaction = new UserInteraction();

        // cria interaçao
        interaction.setUser(loggedUser);
        interaction.setPost(post);
        interaction.setType(InteractionType.COMMENT);


        userInteractionRepository.save(interaction);
    }

    // TODO - futuras formas de interações no futuro



}
