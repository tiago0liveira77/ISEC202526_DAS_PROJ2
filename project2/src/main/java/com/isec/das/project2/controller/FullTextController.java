package com.isec.das.project2.controller;

import com.isec.das.project2.model.FullText;
import com.isec.das.project2.model.Livro;
import com.isec.das.project2.repository.FullTextRepository;
import com.isec.das.project2.repository.LivroRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livros/{id}/fulltext")
public class FullTextController {

    private final LivroRepository livroRepository;
    private final FullTextRepository fullTextRepository;

    public FullTextController(LivroRepository livroRepository,
                              FullTextRepository fullTextRepository) {
        this.livroRepository = livroRepository;
        this.fullTextRepository = fullTextRepository;
    }

    @GetMapping
    public ResponseEntity<String> obterTexto(@PathVariable Long id) {

        FullText ft = fullTextRepository.findById(id).orElse(null);

        if (ft == null) {
            return ResponseEntity.ok(""); // texto vazio por defeito
        }

        return ResponseEntity.ok(ft.getTexto());
    }

    @PutMapping
    public ResponseEntity<String> atualizarTexto(
            @PathVariable Long id,
            @RequestBody String novoTexto) {

        Livro livro = livroRepository.findById(id).orElse(null);
        if (livro == null) {
            return ResponseEntity.notFound().build();
        }

        FullText ft = fullTextRepository.findById(id).orElse(null);

        if (ft == null) {
            ft = new FullText();
            ft.setLivro(livro);
            ft.setTexto(novoTexto);
        } else {
            ft.setTexto(novoTexto);
        }

        fullTextRepository.save(ft);
        return ResponseEntity.ok(novoTexto);
    }
}
