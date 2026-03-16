package com.example.demo.service;

import java.util.List;



import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service // definir que é uma regra de negocio
public class UserService {
    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // comparar hash de senha

    //No Spring, injeção de dependência é o que permite que você “use” um objeto sem precisar instanciá-lo com new, deixando o próprio Spring cuidar da criação e do ciclo de vida dele.
    // injeção do repository
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // retorna uma lista de obejtos User
    public List<User> getAllUsers(){
        return repository.findAll();
    }

    // criar usuario, retorna User e recebe um objeto User
    public User createUser(User user){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // criptografa a senha
        String senhaHash = encoder.encode(user.getSenha());

        // substitui a senha normal pela criptografada
        user.setSenha(senhaHash);
        
        return repository.save(user); // salva o objeto recebido
    }
                            

    // excluir usuario
    // retorna void pois o delete não precisa retornar nada na controller apenas 204
    public void deleteUser(Long id){
        repository.deleteById(id);
    }


    public User uptadeUser(Long id, User userAtualizado){ // recebr id e o objetodo usuario atulzado
        User user = repository.findById(id) // buscar o usuario pelo id passado pra ter a instacia exata dele
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado")); // caso não ache, retorna uua nova execao

        // passar valores usando set da instancia buscada pelo id usando o get 
        user.setNome(userAtualizado.getNome());
        user.setEmail(userAtualizado.getEmail());
        user.setSenha(userAtualizado.getSenha());


        return repository.save(user); // salva no banco

    }


    public boolean login(String email, String senha){
        // buscar usuario pelo email
        User user = repository.findByEmail(email);

        // caso não exista o email
        if (user == null) {
            return false;
        }

        // compara a senha digitada com a senha criptografada
        // mathces retorna um boleano
        return encoder.matches(senha, user.getSenha());
    }


    
}
