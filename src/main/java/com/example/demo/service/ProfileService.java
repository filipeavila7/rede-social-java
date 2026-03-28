package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.FollowingProfileResponse;
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
    public Profile getMyProfile() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UsuÃ¡rio nÃ£o encontrado");
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
            return profileRepository.save(profile);
        }

        // Se o status expirou (24h), apaga antes de retornar.
        clearExpiredStatus(profile);
        return profile;
    }

    // Busca o perfil de outro usuario pelo email.
    // Aplica a mesma regra de expirar o status.
    public Profile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nÃo encontrado");
        }
        Profile profile = user.getProfile();
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil nÃo encontrado");
        }
        clearExpiredStatus(profile);
        return profile;
    }

    // Atualiza perfil do usuario logado.
    // Se o status veio vazio, apaga; se veio preenchido, grava hora de criacao.
    public Profile updateMyProfile(Profile profileAtualizado) {
        Profile profile = getMyProfile();

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
        // pegar email do user logado
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User me = userRepository.findByEmail(email);
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UsuÃ¡rio nÃ£o encontrado");
        }

        // procurar no banco os usuarios que o user logado segue
        return followRepository.findByFollowerId(me.getId())
                .stream()
                .map(f -> {
                    User u = f.getFollowed(); // pegar seguido
                    Profile p = u.getProfile(); // pegar profile do seguido
                    String img = p != null ? p.getImageUrlProfile() : null; // foto de perfil
                    String status = p != null ? getActiveStatus(p) : null;
                    return new FollowingProfileResponse(u.getId(), u.getNome(), img, status); // retorna um dto
                })
                .toList(); // converte em lista
    }

    // Remove status expirado (mais de 24h) e salva a limpeza.
    private void clearExpiredStatus(Profile profile) {
        if (profile.getMessageStatus() == null) return; // se não tiver mensagem, não faz nada
        LocalDateTime createdAt = profile.getMessageStatusCreatedAt(); // pegar data da msg criada
        if (createdAt == null) { // se não tiver data, apaga pq n da para saber se expirou
            profile.setMessageStatus(null);
            profile.setMessageStatusCreatedAt(null);
            profileRepository.save(profile);
            return;
        }
            // se for mais antigo que 24 horas, apaga a mensagem de status
        if (createdAt.isBefore(LocalDateTime.now().minusHours(24))) {
            profile.setMessageStatus(null);
            profile.setMessageStatusCreatedAt(null);
            profileRepository.save(profile);
        }
    }

    // Retorna o status apenas se estiver dentro de 24h.
    private String getActiveStatus(Profile profile) {
        String status = profile.getMessageStatus(); // pega o status
        if (status == null) return null; // se não tem, retrona null
        LocalDateTime createdAt = profile.getMessageStatusCreatedAt();
        if (createdAt == null) return null;
        // se a data for mais antiga que 24 retorna null caso n, retorna a mensagem
        return createdAt.isBefore(LocalDateTime.now().minusHours(24)) ? null : status;
    }
}
