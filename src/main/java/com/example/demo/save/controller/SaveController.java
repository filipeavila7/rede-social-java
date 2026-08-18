package com.example.demo.save.controller;

import com.example.demo.save.dto.SaveResponse;
import com.example.demo.save.service.SaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/save")
@RequiredArgsConstructor
public class SaveController {
    private final SaveService saveService;


    @GetMapping()
    public ResponseEntity<Page<SaveResponse>> getMySaves(
            @PageableDefault(size = 11)
            Pageable pageable
    ){
        return ResponseEntity.ok(saveService.getMySaves(pageable));
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Void> crateSave(
            @PathVariable Long postId
    ){
        saveService.createSave(postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteSave(
            @PathVariable Long postId
    ){
        saveService.deleteSave(postId);
        return ResponseEntity.noContent().build();
    }

}
