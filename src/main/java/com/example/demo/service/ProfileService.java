package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.FollowingProfileResponse;
import com.example.demo.dto.ProfileResponse;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.FollowRepository;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository,
            FollowRepository followRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    // Busca o perfil do usuario logado.
    // Se nao existir, cria um perfil vazio e salva.
    public ProfileResponse getMyProfile() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario nao encontrado");
        }

        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setBio("");
            profile.setImageUrlProfile(null);
            profile.setMessageStatus(null);
            profile.setMessageStatusCreatedAt(null);
            profile.setUser(user);
            user.setProfile(profile);
            Profile saved = profileRepository.save(profile);
            return toResponse(user, saved);
        }

        clearExpiredStatus(profile);
        return toResponse(user, profile);
    }

    // Busca o perfil de outro usuario pelo email.
    // Aplica a mesma regra de expirar o status.
    public ProfileResponse getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado");
        }
        Profile profile = user.getProfile();
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil nao encontrado");
        }
        clearExpiredStatus(profile);
        return toResponse(user, profile);
    }

    // Atualiza perfil do usuario logado.
    // Se o status veio vazio, apaga se veio preenchido, grava hora de criacao.
    public Profile updateMyProfile(Profile profileAtualizado) {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario nao encontrado");
        }
        Profile profile = user.getProfile();
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil nao encontrado");
        }

        // atualiza a bio e foto de perfil
        profile.setBio(profileAtualizado.getBio());
        profile.setImageUrlProfile(profileAtualizado.getImageUrlProfile());

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

    // Lista perfis dos usuarios que o logado segue.
    // Retorna apenas id, nome, foto e status (se ainda estiver valido).
    public List<FollowingProfileResponse> getFollowingProfiles() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User me = userRepository.findByEmail(email);
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario nao encontrado");
        }

        return followRepository.findByFollowerId(me.getId())
                .stream()
                .map(f -> {
                    User u = f.getFollowed();
                    Profile p = u.getProfile();
                    String img = p != null ? p.getImageUrlProfile() : null;
                    String status = p != null ? getActiveStatus(p) : null;
                    return new FollowingProfileResponse(u.getId(), u.getNome(), img, status);
                })
                .toList();
    }

    private ProfileResponse toResponse(User user, Profile profile) {
        return new ProfileResponse(
                user.getNome(),
                profile.getBio(),
                profile.getImageUrlProfile(),
                getActiveStatus(profile)
        );
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
