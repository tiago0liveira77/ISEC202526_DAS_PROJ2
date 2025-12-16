package com.isec.das.project2.controller;

import com.isec.das.project2.model.Livro;
import com.isec.das.project2.repository.LivroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

import static com.isec.das.project2.util.FieldMasks.aplicarFieldMask;

@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroRepository livroRepository;

    public LivroController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "fields", defaultValue = "*") Set<String> fields) {

        size = Math.min(size, 10);

        Pageable pageable = PageRequest.of(page, size);
        Page<Livro> pageResult = livroRepository.findAll(pageable);

        List<Map<String, Object>> items = pageResult.getContent()
                .stream()
                .map(livro -> aplicarFieldMask(livro, fields))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("page", pageResult.getNumber());
        response.put("size", pageResult.getSize());
        response.put("hasNext", pageResult.hasNext());
        response.put("items", items);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obter(@PathVariable Long id,
                                       @RequestParam(value = "fields", defaultValue = "*") Set<String> fields) {
        return livroRepository.findById(id)
                .map(livro -> {
                    Map<String, Object> response = aplicarFieldMask(livro, fields);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public  ResponseEntity<Livro> criar(@RequestBody Livro livro) {
        Livro novo = livroRepository.save(livro);
        URI location = URI.create("/livros/" + novo.getId());
        return ResponseEntity.created(location).body(novo);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Livro> atualizar(
            @PathVariable Long id,
            @RequestBody Livro dados) {

        return livroRepository.findById(id).map(livro -> {
            if (dados.getTitulo() != null) livro.setTitulo(dados.getTitulo());
            if (dados.getAutor() != null) livro.setAutor(dados.getAutor());
            return ResponseEntity.ok(livroRepository.save(livro));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        livroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }



}
