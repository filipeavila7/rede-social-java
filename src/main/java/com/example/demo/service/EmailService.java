package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    // injeção de dependência para enviar email
    private final JavaMailSender mailSender;

    // metodo para enviar email, recebendo o destinatário e o link
    public void enviarEmailReset(String email, String link) {

        // criar uma nova mensagem para ir no email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Redefinição de senha");
        message.setText("Clique para redefinir sua senha: " + link);

        // envia
        mailSender.send(message);
    }
}