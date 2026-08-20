package com.example.demo.tag.service;

import com.example.demo.tag.entity.Tag;
import com.example.demo.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    // TODO: Normalizar as tags informadas pelo usuário e verificar se já existem no banco.
    // Se a tag já existir, reutilizá-la; caso contrário, criar uma nova tag.

    private final TagRepository tagRepository;

    // metodo para normalizar tag (remove # e deixar minusculo)
    private String normalizeTag(String tag) {
        return tag
                .trim()
                .toLowerCase()
                .replaceFirst("^#", "");
    }

    // criar tag normalizada e retorna um set para não ter repetidas (usar metodo na criação de post no set de tags)
    public Set<Tag> createTags(List<String> tags) {

        return tags.stream()
                .map(this::normalizeTag)
                .map(tagName -> tagRepository.findByName(tagName)
                        // se não existir cria uma nova
                        .orElseGet(() -> tagRepository.save(new Tag(null, tagName))))
                .collect(Collectors.toSet());
    }



}
