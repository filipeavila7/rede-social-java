package com.example.demo.controller;

import java.security.Provider.Service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.requests.LoginRequest;
import com.example.demo.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth") // endpoint base
public class AuthController {
    private UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) { // recebr o request de login em json e serializando para objeto
        boolean autenticado = service.login(request.getEmail(), request.getSenha()); // usar o metodo de login
        // na service ele encontra o user pelo email passado, trnasformando em um objeto do tipo user
        // compara a senha digitada com a que esta no banco
        // caso a senha bata, retorna true, caso não, retorna falso
        if (autenticado) { // se oretorno da sevice for true
            return ResponseEntity.ok("Login realizado");
        }
        // caso seja falso 
        return ResponseEntity.status(401).body("Email ou senha incorretos");
       
    }
    
}
