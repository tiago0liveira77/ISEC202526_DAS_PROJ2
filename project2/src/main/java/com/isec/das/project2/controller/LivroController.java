package com.isec.das.project2.controller;

import com.isec.das.project2.model.Livro;
import com.isec.das.project2.repository.LivroRepository;
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
@RequestMapping("/livros")
public class LivroController {

    private final LivroRepository livroRepository;

    public LivroController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @GetMapping
    public Map<String, Object> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Set<String> fields) {

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

        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> obter(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(ResponseEntity::ok)
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

    private Map<String, Object> aplicarFieldMask(Livro livro, Set<String> fields) {

        Map<String, Object> map = new HashMap<>();

        if (fields == null || fields.isEmpty()) {
            map.put("id", livro.getId());
            map.put("titulo", livro.getTitulo());
            map.put("autor", livro.getAutor());
            return map;
        }

        if (fields.contains("id")) {
            map.put("id", livro.getId());
        }
        if (fields.contains("titulo")) {
            map.put("titulo", livro.getTitulo());
        }
        if (fields.contains("autor")) {
            map.put("autor", livro.getAutor());
        }

        return map;
    }

}
