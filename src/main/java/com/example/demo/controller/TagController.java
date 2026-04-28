package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Tag;
import com.example.demo.repository.TagRepository;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagRepository repository;

    public TagController(TagRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Tag> getAllTags(){
        return repository.findAll();
    }
}