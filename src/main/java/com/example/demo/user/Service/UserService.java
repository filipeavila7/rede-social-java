package com.example.demo.user.Service;

import java.util.List;

import com.example.demo.user.dto.UserDto;
import com.example.demo.user.entity.Role;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Profile;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtService jtwService;
    private final UserRepository repository;
    private final GlobalHelperService globalHelperService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // comparar hash de senha

    // retorna uma lista de obejtos User
    public List<User> getAllUsers(){
        return repository.findAll();
    }

    // retorna o usuario logado pelo email do token
    public User getMe() {
        return globalHelperService.getLoggedUser();

    }

    // criar usuario, retorna User e recebe um objeto User
    public User createUser(UserDto dto){
        // instanciar o user
       User user = new User();

       // set dos valores com base no dto recebido
       user.setNome(dto.nome());
       user.setUserName(dto.userName());
       user.setEmail(dto.email());

       // os usarios novos nascem como USER
       user.setRole(Role.USER);

       // criptografar a senha
        String senhaHash = encoder.encode(dto.senha());
        user.setSenha(senhaHash);

        // cria profile automaticamente
        Profile profile = new Profile("", null, null, user);
        user.setProfile(profile);

        // salva o user no banco
        return repository.save(user);

    }
                            

    // excluir usuario
    // retorna void pois o delete não precisa retornar nada na controller apenas 204
    public void deleteUser(Long id){
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = repository.findByEmail(email);

        if (!user.getId().equals(id)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode apagar outro usuário");
        }
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        repository.deleteById(id);
    }


    // editar
    public User uptadeUser(Long id, User userAtualizado){ // recebr id e o objetodo usuario atulzado
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = repository.findByEmail(email);

        if (!user.getId().equals(id)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode editar outro usuário");
        }

        // atualiza apenas os campos enviados para não sobrescrever com null
        if (userAtualizado.getNome() != null && !userAtualizado.getNome().isBlank()) {
            user.setNome(userAtualizado.getNome());
        }

        if (userAtualizado.getEmail() != null && !userAtualizado.getEmail().isBlank()) {
            user.setEmail(userAtualizado.getEmail());
        }

        if (userAtualizado.getUserName() != null && !userAtualizado.getUserName().isBlank()) {
            user.setUserName(userAtualizado.getUserName());
        }

        // gera o hash de novo
        if (userAtualizado.getSenha() != null && !userAtualizado.getSenha().isBlank()) {
            user.setSenha(encoder.encode(userAtualizado.getSenha()));
        }


        return repository.save(user); // salva no banco

    }


    public String login(String email, String senha){
        // buscar usuario pelo email
        User user = repository.findByEmail(email);

        // caso não exista o email
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        // compara a senha digitada com a senha criptografada
        if (!encoder.matches(senha, user.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha inválida");
        }   
        
        // gerar o token com o email existente
        return jtwService.gerarToken(user);
    }


    
}
