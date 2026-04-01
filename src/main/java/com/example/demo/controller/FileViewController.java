package com.example.demo.controller;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
public class FileViewController {

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws Exception {
        Path path = Paths.get("uploads").toAbsolutePath().normalize().resolve(filename);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Só serve se for imagem
        String name = filename.toLowerCase();
        if (!(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"))) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(resource);
    }
}
