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
import java.util.*;

import static com.isec.das.project2.util.FieldMasks.aplicarFieldMask;

@RestController
@RequestMapping("/bibliotecas")
public class BibliotecaController {

    private final BibliotecaRepository bibliotecaRepository;

    public BibliotecaController(BibliotecaRepository bibliotecaRepository) {
        this.bibliotecaRepository = bibliotecaRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Set<String> fields) {

        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size);

        Page<Biblioteca> pageResult;

        if (location != null) {
            pageResult = bibliotecaRepository.findByLocationContainingIgnoreCase(location, pageable);
        } else {
            pageResult = bibliotecaRepository.findAll(pageable);
        }

        List<Map<String, Object>> items = new ArrayList<>();

        for (var b : pageResult.getContent()) {
            items.add(aplicarFieldMask(b, fields));
        }

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

        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findById(id);

        if (optionalBiblioteca.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = aplicarFieldMask(optionalBiblioteca.get(), fields);

        return ResponseEntity.ok(response);
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

        Optional<Biblioteca> optionalBiblioteca = bibliotecaRepository.findById(id);

        if (optionalBiblioteca.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Biblioteca biblioteca = optionalBiblioteca.get();

        if (dados.getNome() != null) {
            biblioteca.setNome(dados.getNome());
        }

        if (dados.getLocalizacao() != null) {
            biblioteca.setLocalizacao(dados.getLocalizacao());
        }

        Biblioteca bibliotecaAtualizada = bibliotecaRepository.save(biblioteca);

        return ResponseEntity.ok(bibliotecaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {

        if (!bibliotecaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        bibliotecaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
