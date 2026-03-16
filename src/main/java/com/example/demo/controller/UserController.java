package com.example.demo.controller;

import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import java.util.List;

import javax.swing.Spring;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/users") // rota base para todos os endpoints
public class UserController {
    private final UserRepository userRepository;
    private final UserService service;


    // injeção da service no construtor
    public UserController(UserService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }


    // metodos http

    // GEt
    @GetMapping // ResponseEntity para retornar uma resposta http completa
    public ResponseEntity<List <User>> getAllUsers(){ // <List<User>> indica que o corpo da resposta vai ser uma lista de objetos User
        List<User> users = service.getAllUsers(); // declarar que essa lista é igual ao metodo da servic de retornar uma lista de usuarios
        return ResponseEntity.ok(users); // retornar a resposta 
    }

    // post
    @PostMapping // O corpo dessa resposta vai ser do tipo User
    public ResponseEntity<User> createUser(@RequestBody User user){ // converter o objeto json em objeto User, usa o jackson para criar a instacia na classe usando o set para cada campo
        User createdUser = service.createUser(user); // criar uma variavel do tipo User é igual ao meotdo de createUser, passando o json convertido como parametro
        // jackson pega o objeto e trnaforma em json usando os getter para cada atributo e retorna um json como corpo e com status
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser); //retornar o corpo da resposta (created user) e o status da requisição
    }
    

    // Isso é importante para o Spring/Jackson:

    // Ele precisa saber que tipo de objeto ele vai serializar em JSON.

    // Ele chama os getters do User para montar o JSON corretamente.

    // Sem isso, o Spring não sabe que tipo está sendo retornado e pode precisar inferir, ou até dar warnings.


    // deletar
    @DeleteMapping("/{id}") // rota delete /users/id
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){  // pegar valor do id direto da url{

        service.deleteUser(id); //usar metodo da service passando o id que veio da url

        // retorna status HTTP 204 (No Content)
        // significa que a operação funcionou e não há conteúdo para retornar
        return ResponseEntity.noContent().build();
    }


    // editar
    @PutMapping("/{id}")// receber id e objeto json dos dados atualizados
    public ResponseEntity<User> putMethodName(@PathVariable Long id, @RequestBody User user) {

        User usuarioAtualizado = service.uptadeUser(id, user); // usar o metodo de editar passand o id da url e o conteudo  atualizaDO
      
        // retorna status 200 (OK) junto com o usuário atualizado
        return ResponseEntity.ok(usuarioAtualizado); 
    }





    


}
