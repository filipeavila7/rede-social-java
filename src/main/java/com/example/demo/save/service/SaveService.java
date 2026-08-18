package com.example.demo.save.service;

import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.post.entity.Post;
import com.example.demo.save.dto.SaveResponse;
import com.example.demo.save.entity.Save;
import com.example.demo.save.mapper.SaveMapper;
import com.example.demo.save.repository.SaveRepository;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SaveService {
    private final SaveRepository saveRepository;
    private final GlobalHelperService globalHelperService;
    private final SaveMapper saveMapper;

    // mostrar posts salvos do user logado
    public Page<SaveResponse> getMySaves(Pageable pageable){
        User loggedUser = globalHelperService.getLoggedUser();

        return saveRepository.findByUserId(loggedUser.getId(), pageable)
                .map(saveMapper::toSaveResponse);
    }

    // salvar um post
    public void createSave(Long postId){
        User loggedUser = globalHelperService.getLoggedUser();

        Post post = globalHelperService.findPostById(postId);

        // cria o salvamento
        Save save = new Save();

        // relaciona
        save.setCreateAt(LocalDateTime.now());
        save.setPost(post);
        save.setUser(loggedUser);

        saveRepository.save(save);

    }

    // remover salvo
    public void deleteSave(Long postId){
        Save save = saveRepository.findByUserIdAndPostId(
                globalHelperService.getLoggedUser().getId(), postId
        ).orElseThrow(() -> new RuntimeException("Save não encontrado"));

        saveRepository.delete(save);
    }



}
