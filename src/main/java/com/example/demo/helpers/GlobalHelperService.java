package com.example.demo.helpers;

import com.example.demo.entity.User;
import com.example.demo.exeptions.user.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GlobalHelperService {
    private final UserRepository userRepository;

    // pegar usuario logado
    public User getLoggedUser() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

       return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }
}
