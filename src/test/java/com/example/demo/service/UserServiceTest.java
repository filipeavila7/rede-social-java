package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class UserServiceTest {

    @Test
    void uptadeUserMantemCamposNaoEnviados() {
        UserRepository repository = mock(UserRepository.class);
        JwtService jwtService = mock(JwtService.class);
        UserService service = new UserService(repository, jwtService);

        User existente = new User("Nome antigo", "antigo@email.com", new BCryptPasswordEncoder().encode("senha123"));
        User atualizacao = new User();
        atualizacao.setNome("Nome novo");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = service.uptadeUser(1L, atualizacao);

        assertEquals("Nome novo", resultado.getNome());
        assertEquals("antigo@email.com", resultado.getEmail());
        assertEquals(existente.getSenha(), resultado.getSenha());
        verify(repository).save(existente);
    }

    @Test
    void uptadeUserAtualizaSenhaQuandoEnviada() {
        UserRepository repository = mock(UserRepository.class);
        JwtService jwtService = mock(JwtService.class);
        UserService service = new UserService(repository, jwtService);

        User existente = new User("Nome", "email@email.com", new BCryptPasswordEncoder().encode("senhaAntiga"));
        String senhaAntigaHash = existente.getSenha();
        User atualizacao = new User();
        atualizacao.setSenha("novaSenha123");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = service.uptadeUser(1L, atualizacao);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        assertNotEquals(senhaAntigaHash, resultado.getSenha());
        assertTrue(encoder.matches("novaSenha123", resultado.getSenha()));
    }
}
