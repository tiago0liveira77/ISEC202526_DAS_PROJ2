package com.isec.das.project2.controller;

import com.isec.das.project2.model.*;
import com.isec.das.project2.repository.*;
import com.isec.das.project2.util.EstadoEmprestimo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoRepository emprestimoRepository;
    private final PessoaRepository pessoaRepository;
    private final CopiaLivroRepository copiaRepository;
    private final RegistoRepository registoRepository;

    public EmprestimoController(
            EmprestimoRepository emprestimoRepository,
            PessoaRepository pessoaRepository,
            CopiaLivroRepository copiaRepository,
            RegistoRepository registoRepository) {

        this.emprestimoRepository = emprestimoRepository;
        this.pessoaRepository = pessoaRepository;
        this.copiaRepository = copiaRepository;
        this.registoRepository = registoRepository;
    }

    @PostMapping
    public ResponseEntity<Emprestimo> criar(
            @RequestParam Long pessoaId,
            @RequestParam Long copiaId) {

        Pessoa pessoa = pessoaRepository.findById(pessoaId).orElse(null);
        CopiaLivro copia = copiaRepository.findById(copiaId).orElse(null);

        if (pessoa == null || copia == null) {
            return ResponseEntity.badRequest().build();
        }

        // verificar se a pessoa está registada na biblioteca desta cópia
        boolean registado = registoRepository
                .findByPessoaId(pessoaId).stream()
                .anyMatch(r ->
                        r.getBiblioteca().getId().equals(copia.getBiblioteca().getId())
                                && r.getEstado().name().equals("ATIVO")
                );

        if (!registado) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // criar empréstimo
        Emprestimo e = emprestimoRepository.save(Emprestimo.builder()
                .pessoa(pessoa)
                .copiaLivro(copia)
                .estado(EstadoEmprestimo.ATIVO)
                .dataEmprestimo(LocalDate.now())
                .build());
        URI location = URI.create("/emprestimos/" + e.getId());
        return ResponseEntity.created(location).body(e);
    }

    @GetMapping
    public List<Emprestimo> listar(
            @RequestParam(required = false) Long pessoaId,
            @RequestParam(required = false) Boolean ativos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        size = Math.min(size, 10);

        List<Emprestimo> lista;

        if (pessoaId != null && Boolean.TRUE.equals(ativos)) {
            lista = emprestimoRepository
                    .findByPessoaIdAndEstado(pessoaId, EstadoEmprestimo.ATIVO);
        } else if (pessoaId != null) {
            lista = emprestimoRepository.findByPessoaId(pessoaId);
        } else {
            lista = emprestimoRepository.findAll();
        }

        int start = page * size;
        if (start >= lista.size()) {
            return List.of();
        }

        int end = Math.min(start + size, lista.size());
        return lista.subList(start, end);
    }


    @PatchMapping("/{id}:devolver")
    public ResponseEntity<?> devolver(@PathVariable Long id) {
        return emprestimoRepository.findById(id).map(emp -> {
            if (emp.getEstado() != EstadoEmprestimo.ATIVO) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            emp.setEstado(EstadoEmprestimo.DEVOLVIDO);
            emp.setDataDevolucao(LocalDate.now());
            return ResponseEntity.ok(emprestimoRepository.save(emp));
        }).orElse(ResponseEntity.notFound().build());
    }
}
