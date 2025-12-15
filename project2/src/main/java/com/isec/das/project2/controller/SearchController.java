package com.isec.das.project2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isec.das.project2.model.Operation;
import com.isec.das.project2.repository.FullTextRepository;
import com.isec.das.project2.repository.LivroRepository;
import com.isec.das.project2.repository.OperationRepository;
import com.isec.das.project2.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/searchFulltext")
public class SearchController {

    private final LivroRepository livroRepository;
    private final FullTextRepository fullTextRepository;
    private final OperationRepository operationRepository;
    private final ObjectMapper mapper;

    private final SearchService searchService;

    public SearchController(LivroRepository livroRepository,
                            FullTextRepository fullTextRepository,
                            OperationRepository operationRepository,
                            ObjectMapper mapper, SearchService searchService) {
        this.livroRepository = livroRepository;
        this.fullTextRepository = fullTextRepository;
        this.operationRepository = operationRepository;
        this.mapper = mapper;
        this.searchService = searchService;
    }

    @PostMapping
    public ResponseEntity<Operation> iniciarPesquisa(@RequestParam String word) {

        Operation op = operationRepository.save(Operation.builder()
                .done(false)
                .build());

        searchService.executarPesquisaAsync(
                op.getId(),
                word,
                livroRepository,
                fullTextRepository,
                operationRepository,
                mapper
        );

        return ResponseEntity.ok(op);
    }

}
