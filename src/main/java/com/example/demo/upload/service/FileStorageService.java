package com.example.demo.upload.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.example.demo.exeptions.api.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileStorageService {

    // Pasta onde os arquivos serao salvos (relativa ao backend).
    private final Path uploadDir = Paths
            .get("uploads")
            .toAbsolutePath()
            .normalize();

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

        System.out.println("ARQUIVO SALVO EM: " + target.toAbsolutePath());
        System.out.println("ARQUIVO EXISTE: " + Files.exists(target));

        // Retorna a URL publica para salvar no banco.
        return "/files/" + filename;
    }

    public String savePrivateStory(MultipartFile file) throws IOException {
        Path storyDir = Paths
                .get("uploads/private/stories")
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(storyDir);

        String original = file.getOriginalFilename();
        String extension = "";

        if (original != null) {
            int dot = original.lastIndexOf('.');

            if (dot >= 0 && dot < original.length() - 1) {
                extension = original
                        .substring(dot)
                        .replaceAll("[^A-Za-z0-9\\.]", "");
            }
        }

        String filename = UUID.randomUUID()
                .toString()
                .replace("-", "") + extension;

        Path target = storyDir.resolve(filename);

        Files.copy(
                file.getInputStream(),
                target,
                StandardCopyOption.REPLACE_EXISTING
        );

        return filename;
    }


    public Resource loadPrivateStory(String filename) {

        Path storyDir = Paths
                .get("uploads/private/stories")
                .toAbsolutePath()
                .normalize();

        Path file = storyDir.resolve(filename).normalize();

        try {
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Arquivo não encontrado");
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao carregar arquivo", e);
        }
    }
}
