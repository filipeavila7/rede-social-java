package com.example.demo.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeia /uploads/** para a pasta fisica uploads/ no servidor.
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        String location = "file:" + uploadDir.toString() + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
        // Mapeia /files/** para a mesma pasta de uploads.
        registry.addResourceHandler("/files/**")
                .addResourceLocations(location);
    }
}
