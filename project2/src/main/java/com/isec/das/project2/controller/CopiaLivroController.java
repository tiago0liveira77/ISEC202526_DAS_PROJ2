package com.isec.das.project2.controller;

import com.isec.das.project2.model.*;
import com.isec.das.project2.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.isec.das.project2.util.EstadoEmprestimo;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/copias")
public class CopiaLivroController {

    private final CopiaLivroRepository copiaRepository;
    private final LivroRepository livroRepository;
    private final BibliotecaRepository bibliotecaRepository;
    private final EmprestimoRepository emprestimoRepository;
    public CopiaLivroController(
            CopiaLivroRepository copiaRepository,
            LivroRepository livroRepository,
            BibliotecaRepository bibliotecaRepository, EmprestimoRepository emprestimoRepository) {

        this.copiaRepository = copiaRepository;
        this.livroRepository = livroRepository;
        this.bibliotecaRepository = bibliotecaRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @PostMapping
    public ResponseEntity<CopiaLivro> criar(
            @RequestParam Long livroId,
            @RequestParam Long bibliotecaId) {

        Livro livro = livroRepository.findById(livroId).orElse(null);
        Biblioteca biblioteca = bibliotecaRepository.findById(bibliotecaId).orElse(null);

        if (livro == null || biblioteca == null) {
            return ResponseEntity.badRequest().build();
        }

        CopiaLivro copia = copiaRepository.save(
                CopiaLivro.builder()
                        .livro(livro)
                        .biblioteca(biblioteca)
                        .build());
        URI location = URI.create("/copias/" + copia.getId());
        return ResponseEntity.created(location).body(copia);
    }

    @GetMapping
    public List<CopiaLivro> listar(
            @RequestParam(required = false) Long bibliotecaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        size = Math.min(size, 10);

        List<CopiaLivro> lista;

        if (bibliotecaId != null) {
            lista = copiaRepository.findByBibliotecaId(bibliotecaId);
        } else {
            lista = copiaRepository.findAll();
        }

        int start = page * size;
        if (start >= lista.size()) {
            return List.of();
        }

        int end = Math.min(start + size, lista.size());
        return lista.subList(start, end);
    }

    @PostMapping("/{id}:move")
    public ResponseEntity<CopiaLivro> moverCopia(
            @PathVariable Long id,
            @RequestParam Long novaBibliotecaId) {

        CopiaLivro copia = copiaRepository.findById(id).orElse(null);
        if (copia == null) {
            return ResponseEntity.notFound().build();
        }

        Biblioteca novaBiblioteca = bibliotecaRepository.findById(novaBibliotecaId).orElse(null);
        if (novaBiblioteca == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean emprestada = emprestimoRepository
                .findByCopiaLivroId(id)
                .stream()
                .anyMatch(e -> e.getEstado() == EstadoEmprestimo.ATIVO);

        if (emprestada) {
            // Não permitido mover uma cópia emprestada
            return ResponseEntity.status(403).build();
        }

        copia.setBiblioteca(novaBiblioteca);
        CopiaLivro atualizada = copiaRepository.save(copia);

        return ResponseEntity.ok(atualizada);
    }



}
