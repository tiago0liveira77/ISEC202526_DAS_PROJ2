package com.isec.das.project2.controller;

import com.isec.das.project2.model.*;
import com.isec.das.project2.repository.*;
import com.isec.das.project2.util.EstadoEmprestimo;
import com.isec.das.project2.util.EstadoRegisto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.isec.das.project2.util.FieldMasks.aplicarFieldMask;

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

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) Long pessoaId,
            @RequestParam(required = false) Boolean ativos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size);

        Page<Emprestimo> pageResult;

        if (pessoaId != null && Boolean.TRUE.equals(ativos)) {
            pageResult = emprestimoRepository.findByPessoaIdAndEstado(pessoaId, EstadoEmprestimo.ATIVO, pageable);
        } else if (pessoaId != null) {
            pageResult = emprestimoRepository.findByPessoaId(pessoaId, pageable);
        } else {
            pageResult = emprestimoRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("page", pageResult.getNumber());
        response.put("size", pageResult.getSize());
        response.put("hasNext", pageResult.hasNext());
        response.put("items", pageResult.getContent());

        return ResponseEntity.ok(response);
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

        boolean registado = false;

        for (Registo registo : registoRepository.findByPessoaId(pessoaId)) {
            if (registo.getBiblioteca().getId().equals(copia.getBiblioteca().getId())
                    && registo.getEstado() == EstadoRegisto.ATIVO) {
                registado = true;
                break;
            }
        }

        if (!registado) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // criar empréstimo
        Emprestimo emprestimo = emprestimoRepository.save(Emprestimo.builder()
                .pessoa(pessoa)
                .copiaLivro(copia)
                .estado(EstadoEmprestimo.ATIVO)
                .dataEmprestimo(LocalDate.now())
                .build());

        URI location = URI.create("/emprestimos/" + emprestimo.getId());
        return ResponseEntity.created(location).body(emprestimo);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> obter(@PathVariable Long id,
                                   @RequestParam(value = "fields", defaultValue = "*") Set<String> fields) {

        Optional<Emprestimo> optionalEmprestimo = emprestimoRepository.findById(id);

        if (optionalEmprestimo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = aplicarFieldMask(optionalEmprestimo.get(), fields);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}:devolver")
    public ResponseEntity<?> devolver(@PathVariable Long id) {

        Optional<Emprestimo> optionalEmprestimo = emprestimoRepository.findById(id);

        if (optionalEmprestimo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Emprestimo emp = optionalEmprestimo.get();

        if (emp.getEstado() != EstadoEmprestimo.ATIVO) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        emp.setEstado(EstadoEmprestimo.DEVOLVIDO);
        emp.setDataDevolucao(LocalDate.now());

        Emprestimo emprestimoAtualizado = emprestimoRepository.save(emp);

        return ResponseEntity.ok(emprestimoAtualizado);

    }

}
