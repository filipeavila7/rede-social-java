package com.example.demo.profile.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.exeptions.profile.ProfileNotFoundException;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.profile.dto.ProfileUpdateRequest;
import com.example.demo.profile.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.FollowingProfileResponse;
import com.example.demo.profile.dto.ProfileResponse;
import com.example.demo.profile.entity.Profile;
import com.example.demo.user.entity.User;
import com.example.demo.follow.repository.FollowRepository;
import com.example.demo.profile.repository.ProfileRepository;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.util.FileUrlUtils;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final GlobalHelperService globalHelperService;
    private final ProfileMapper profileMapper;

    // Busca o perfil do usuario logado, se não existir, cria um perfil vazio e salva.
    public ProfileResponse getMyProfile() {
        User loggedUser = globalHelperService.getLoggedUser();

        Profile profile = loggedUser.getProfile();

        if (profile == null) {
            profile = new Profile();
            profile.setBio("");
            profile.setImageUrlProfile(null);
            profile.setMessageStatus(null);
            profile.setMessageStatusCreatedAt(null);
            profile.setUser(loggedUser);
            loggedUser.setProfile(profile);

            return profileMapper.toProfileResponse(profileRepository.save(profile));
        }

        clearExpiredStatus(profile);
        return profileMapper.toProfileResponse(profile);
    }

    // metodo para pesquisar usuarios pelo userName
    public List<ProfileResponse> searchProfiles(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return List.of();
        }
        // retorna uma profileResponse so com os campos que queremos
        return profileRepository
                .findByUser_userNameContainingIgnoreCase(termo.trim())
                .stream()
                .map(profile -> toResponse(profile.getUser(), profile))
                .toList();
    }

    // Busca o perfil de outro usuario pelo email.
    // Aplica a mesma regra de expirar o status.
    public ProfileResponse getProfileByUserName(String userName) {

        User user = userRepository.findByuserName(userName).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuario nao encontrado"
        ));

        Profile profile = user.getProfile();
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil nao encontrado");
        }
        clearExpiredStatus(profile);
        return toResponse(user, profile);
    }

    // Atualiza perfil do usuario logado.
    // Se o status veio vazio, apaga se veio preenchido, grava hora de criacao.
    public ProfileResponse updateMyProfile(ProfileUpdateRequest request) {
        // pega o user logado
        User loggedUser = globalHelperService.getLoggedUser();

        // encontra a profile
        Profile profile = profileRepository.findByUserId(loggedUser.getId())
                .orElseThrow(ProfileNotFoundException::new);


        // atualiza a bio
       if (request.bio() != null){
           profile.setBio(request.bio());
       }

        // só atualiza a foto quando vier uma URL persistível; preview blob do navegador não deve ir para o banco
        String imageUrlProfile = profileAtualizado.getImageUrlProfile();
        if (imageUrlProfile != null) {
            String normalizedImageUrl = FileUrlUtils.normalizeStoredPath(imageUrlProfile);
            if (normalizedImageUrl != null && !normalizedImageUrl.contains("blob:")) {
                profile.setImageUrlProfile(normalizedImageUrl);
            }
        }

        // atualiza o status
        String status = profileAtualizado.getMessageStatus();
        if (status != null && !status.isBlank()) {
            profile.setMessageStatus(status);
            profile.setMessageStatusCreatedAt(LocalDateTime.now());
        } else {
            profile.setMessageStatus(null);
            profile.setMessageStatusCreatedAt(null);
        }

        return profileRepository.save(profile);
    }


    // Remove status expirado (mais de 24h) e salva a limpeza.
    private void clearExpiredStatus(Profile profile) {
        if (profile.getMessageStatus() == null) return;
        LocalDateTime createdAt = profile.getMessageStatusCreatedAt();
        if (createdAt == null) {
            profile.setMessageStatus(null);
            profile.setMessageStatusCreatedAt(null);
            profileRepository.save(profile);
            return;
        }
        if (createdAt.isBefore(LocalDateTime.now().minusHours(24))) {
            profile.setMessageStatus(null);
            profile.setMessageStatusCreatedAt(null);
            profileRepository.save(profile);
        }
    }

    // Retorna o status apenas se estiver dentro de 24h.
    private String getActiveStatus(Profile profile) {
        String status = profile.getMessageStatus();
        if (status == null) return null;
        LocalDateTime createdAt = profile.getMessageStatusCreatedAt();
        if (createdAt == null) return null;
        return createdAt.isBefore(LocalDateTime.now().minusHours(24)) ? null : status;
    }
}
