package com.example.demo.tag.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.tag.entity.Tag;
import com.example.demo.tag.repository.TagRepository;

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