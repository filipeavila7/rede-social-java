package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    // Pasta onde os arquivos serao salvos (relativa ao backend).
    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    public String save(MultipartFile file) throws IOException {
        // Garante que a pasta exista.
        Files.createDirectories(uploadDir);

        // Monta extensao segura a partir do nome original (se existir).
        String original = file.getOriginalFilename();
        String extension = "";
        if (original != null) {
            int dot = original.lastIndexOf('.');
            if (dot >= 0 && dot < original.length() - 1) {
                extension = original.substring(dot).replaceAll("[^A-Za-z0-9\\.]", "");
            }
        }

        // Gera nome unico para evitar colisao.
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = uploadDir.resolve(filename);
        // Salva o arquivo fisico no disco.
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Retorna a URL publica para salvar no banco.
        return "/uploads/" + filename;
    }
}
