package com.isec.das.project2.controller;

import com.isec.das.project2.model.Biblioteca;
import com.isec.das.project2.repository.BibliotecaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bibliotecas")
public class BibliotecaController {

    private final BibliotecaRepository bibliotecaRepository;

    public BibliotecaController(BibliotecaRepository bibliotecaRepository) {
        this.bibliotecaRepository = bibliotecaRepository;
    }

    @GetMapping
    public List<Biblioteca> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        size = Math.min(size, 10);

        List<Biblioteca> lista = bibliotecaRepository.findAll();
        int start = page * size;

        if (start >= lista.size()) return List.of();

        int end = Math.min(start + size, lista.size());
        return lista.subList(start, end);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Biblioteca> obter(@PathVariable Long id) {
        return bibliotecaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Biblioteca criar(@RequestBody Biblioteca biblioteca) {
        return bibliotecaRepository.save(biblioteca);
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
}
