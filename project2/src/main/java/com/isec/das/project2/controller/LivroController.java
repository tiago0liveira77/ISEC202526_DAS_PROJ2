package com.isec.das.project2.controller;

import com.isec.das.project2.model.FullText;
import com.isec.das.project2.model.Livro;
import com.isec.das.project2.repository.FullTextRepository;
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
    private final FullTextRepository fullTextRepository;

    public LivroController(LivroRepository livroRepository, FullTextRepository fullTextRepository) {
        this.livroRepository = livroRepository;
        this.fullTextRepository = fullTextRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String autor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "fields", defaultValue = "*") Set<String> fields) {

        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size);

        Page<Livro> pageResult;

        if (autor != null) {
            pageResult = livroRepository.findByAutorContainingIgnoreCase(autor, pageable);
        } else {
            pageResult = livroRepository.findAll(pageable);
        }

        List<Map<String, Object>> items = new ArrayList<>();

        for (Livro livro : pageResult.getContent()) {
            items.add(aplicarFieldMask(livro, fields));
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

        Optional<Livro> optionalLivro = livroRepository.findById(id);

        if (optionalLivro.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response =
                aplicarFieldMask(optionalLivro.get(), fields);

        return ResponseEntity.ok(response);


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

        Optional<Livro> optionalLivro = livroRepository.findById(id);

        if (optionalLivro.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Livro livro = optionalLivro.get();

        if (dados.getTitulo() != null) {
            livro.setTitulo(dados.getTitulo());
        }
        if (dados.getAutor() != null) {
            livro.setAutor(dados.getAutor());
        }

        Livro livroAtualizado = livroRepository.save(livro);
        return ResponseEntity.ok(livroAtualizado);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        livroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/fulltext")
    public ResponseEntity<String> obterTexto(@PathVariable Long id) {

        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        FullText ft = fullTextRepository.findById(id).orElse(null);

        if (ft == null) {
            return ResponseEntity.ok("");
        }

        return ResponseEntity.ok(ft.getTexto());
    }

    @PutMapping("/{id}/fulltext")
    public ResponseEntity<Void> atualizarTexto(
            @PathVariable Long id,
            @RequestBody String novoTexto) {

        Livro livro = livroRepository.findById(id).orElse(null);
        if (livro == null) {
            return ResponseEntity.notFound().build();
        }

        FullText ft = fullTextRepository.findById(id).orElse(
                FullText.builder().livro(livro).build()
        );

        ft.setTexto(novoTexto);
        fullTextRepository.save(ft);

        return ResponseEntity.noContent().build();
    }


}
