package com.isec.das.project2.controller;

import com.isec.das.project2.model.Biblioteca;
import com.isec.das.project2.model.Livro;
import com.isec.das.project2.repository.BibliotecaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/bibliotecas")
public class BibliotecaController {

    private final BibliotecaRepository bibliotecaRepository;

    public BibliotecaController(BibliotecaRepository bibliotecaRepository) {
        this.bibliotecaRepository = bibliotecaRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Set<String> fields) {

        size = Math.min(size, 10);

        Pageable pageable = PageRequest.of(page, size);
        Page<Biblioteca> pageResult = bibliotecaRepository.findAll(pageable);

        List<Map<String, Object>> items = pageResult.getContent()
                .stream()
                .map(b -> aplicarFieldMask(b, fields))
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
        return bibliotecaRepository.findById(id)
                .map(biblioteca -> {
                    Map<String, Object> response = aplicarFieldMask(biblioteca, fields);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Biblioteca> criar(@RequestBody Biblioteca biblioteca) {
        Biblioteca novo = bibliotecaRepository.save(biblioteca);
        URI location = URI.create("/bibliotecas/" + novo.getId());
        return ResponseEntity.created(location).body(novo);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Biblioteca> atualizar(
            @PathVariable Long id,
            @RequestBody Biblioteca dados) {

        return bibliotecaRepository.findById(id).map(biblioteca -> {
            if (dados.getNome() != null) {
                biblioteca.setNome(dados.getNome());
            }
            if (dados.getLocalizacao() != null) {
                biblioteca.setLocalizacao(dados.getLocalizacao());
            }
            return ResponseEntity.ok(bibliotecaRepository.save(biblioteca));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!bibliotecaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bibliotecaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> aplicarFieldMask(Biblioteca biblioteca, Set<String> fields) {

        Map<String, Object> map = new HashMap<>();

        if (fields.contains("*")) {
            map.put("id", biblioteca.getId());
            map.put("nome", biblioteca.getNome());
            map.put("localizacao", biblioteca.getLocalizacao());
            return map;
        }

        if (fields.contains("id")) {
            map.put("id", biblioteca.getId());
        }
        if (fields.contains("nome")) {
            map.put("nome", biblioteca.getNome());
        }
        if (fields.contains("localizacao")) {
            map.put("localizacao", biblioteca.getLocalizacao());
        }

        return map;
    }
}
