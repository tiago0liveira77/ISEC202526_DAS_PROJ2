package com.isec.das.project2.controller;

import com.isec.das.project2.model.Livro;
import com.isec.das.project2.repository.LivroRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroRepository livroRepository;

    public LivroController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @GetMapping
    public List<Livro> listar( @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {

        size = Math.min(size, 10); // máximo 10

        List<Livro> todos = livroRepository.findAll();
        int start = page * size;

        if (start >= todos.size()) {
            return List.of();
        }

        int end = Math.min(start + size, todos.size());
        return todos.subList(start, end);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> obter(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Livro criar(@RequestBody Livro livro) {
        return livroRepository.save(livro);
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
