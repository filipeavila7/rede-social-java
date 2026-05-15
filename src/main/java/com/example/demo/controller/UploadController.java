package com.example.demo.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.FileStorageService;

@RestController
@RequestMapping("/files")
public class UploadController {

    private final FileStorageService storageService;

    public UploadController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    // POST /files/upload
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file)
            throws IOException {
        // Valida arquivo vazio.
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Arquivo vazio"));
        }

        // valida tamanho (2MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Arquivo maior que 10MB"));
        }

        // valida tipo, aceitar somente png e jpeg
        String type = file.getContentType();
        if (type == null || !(type.equals("image/jpeg") || type.equals("image/png"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Somente PNG ou JPG"));
        }

        // Salva o arquivo e devolve a URL publica.
        String url = storageService.save(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
