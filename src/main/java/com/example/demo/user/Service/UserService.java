package com.example.demo.user.Service;


import com.example.demo.user.dto.UpdateUserRequest;
import com.example.demo.user.dto.UserRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.entity.Role;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.user.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.example.demo.entity.Profile;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GlobalHelperService globalHelperService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    // retorna o usuario logado pelo email do token
    public User getMe() {
        return globalHelperService.getLoggedUser();

    }

    // criar usuario
    @Transactional
    public UserResponse createUser(UserRequest request){
        // instanciar o user
        User user = new User();

        // set dos valores com base no request recebido
        user.setName(request.name());
        user.setUserName(request.userName());
        user.setEmail(request.email());

        // os usarios novos nascem como USER
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.password()));

        // cria profile automaticamente
        Profile profile = new Profile("", null, null, user);
        user.setProfile(profile);


        return userMapper.toUserResponse(userRepository.save(user));

    }
                            

    // próprio usuario se deletar
    public void deleteUser(){
        userRepository.delete(globalHelperService.getLoggedUser());
    }

    // eidtar usuario
    public UserResponse updateUser(UpdateUserRequest request){ // recebr id e o objetodo usuario atulzado
        User loggedUser = globalHelperService.getLoggedUser();

        if (request.userName() != null){
            loggedUser.setUserName(request.userName());
        }

        if (request.name() != null){
            loggedUser.setName(request.name());
        }

        if (request.password() != null){
            loggedUser.setPassword(passwordEncoder.encode(request.password()));
        }

        return userMapper.toUserResponse(userRepository.save(loggedUser));

    }


    
}
