package com.isec.das.project2.controller;

import com.isec.das.project2.model.*;
import com.isec.das.project2.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.isec.das.project2.util.EstadoEmprestimo;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> listar(
            @RequestParam(required = false) Long bibliotecaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size);

        Page<CopiaLivro> pageResult;

        if (bibliotecaId != null) {
            pageResult = copiaRepository.findByBibliotecaId(bibliotecaId, pageable);
        } else {
            pageResult = copiaRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("page", pageResult.getNumber());
        response.put("size", pageResult.getSize());
        response.put("hasNext", pageResult.hasNext());
        response.put("items", pageResult.getContent());

        return ResponseEntity.ok(response);
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

        boolean emprestada = false;

        for (Emprestimo emprestimo : emprestimoRepository.findByCopiaLivroId(id)) {
            if (emprestimo.getEstado() == EstadoEmprestimo.ATIVO) {
                emprestada = true;
                break;
            }
        }

        if (emprestada) {
            // Não permitido mover uma cópia emprestada
            return ResponseEntity.status(403).build();
        }

        copia.setBiblioteca(novaBiblioteca);
        CopiaLivro atualizada = copiaRepository.save(copia);

        return ResponseEntity.ok(atualizada);
    }



}
