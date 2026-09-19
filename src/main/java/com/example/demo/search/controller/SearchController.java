package com.example.demo.search.controller;

import com.example.demo.search.dto.SearchResponse;
import com.example.demo.search.dto.SearchSuggestionsResponse;
import com.example.demo.search.service.SearchService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService service;

    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @RequestParam
            @NotBlank
            String q,

            @PageableDefault(size = 12)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.search(q, pageable)
        );
    }

    @GetMapping("/suggestions")
    public ResponseEntity<SearchSuggestionsResponse> suggestions(
            @RequestParam
            @NotBlank
            String q
    ) {
        return ResponseEntity.ok(
                service.suggestions(q)
        );
    }
}