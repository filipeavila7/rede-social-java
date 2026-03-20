package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    // buscar perfil do usuario logado (cria vazio se nao existir)
    public Profile getMyProfile() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setBio("");
            profile.setImageUrlProfile(null);
            profile.setUser(user);
            user.setProfile(profile);
            return profileRepository.save(profile);
        }

        return profile;
    }

    // atualizar perfil do usuario logado
    public Profile updateMyProfile(Profile profileAtualizado) {
        Profile profile = getMyProfile();

        profile.setBio(profileAtualizado.getBio());
        profile.setImageUrlProfile(profileAtualizado.getImageUrlProfile());

        return profileRepository.save(profile);
    }
}
