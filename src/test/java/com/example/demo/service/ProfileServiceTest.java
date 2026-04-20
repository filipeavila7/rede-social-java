package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.FollowRepository;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.UserRepository;

class ProfileServiceTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateMyProfileIgnoraBlobUrlEPreservaImagemAtual() {
        ProfileRepository profileRepository = mock(ProfileRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        FollowRepository followRepository = mock(FollowRepository.class);
        ProfileService service = new ProfileService(profileRepository, userRepository, followRepository);

        User user = new User("shipin", "shipin@email.com", "senha");
        Profile existente = new Profile();
        existente.setBio("bio antiga");
        existente.setImageUrlProfile("/files/avatar-antigo.png");
        existente.setUser(user);
        user.setProfile(existente);

        Profile atualizado = new Profile();
        atualizado.setBio("Codeiro Java");
        atualizado.setImageUrlProfile("http://localhost:8080/blob:http:/localhost:5173/c925d711-4483-4c9e-a303-8cd5a830bf0a");

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("shipin@email.com", null));

        when(userRepository.findByEmail("shipin@email.com")).thenReturn(user);
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Profile resultado = service.updateMyProfile(atualizado);

        assertEquals("Codeiro Java", resultado.getBio());
        assertEquals("/files/avatar-antigo.png", resultado.getImageUrlProfile());
        verify(profileRepository).save(existente);
    }
}
