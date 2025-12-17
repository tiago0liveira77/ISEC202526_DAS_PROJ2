package com.isec.das.project2.controller;

import com.isec.das.project2.model.Pessoa;
import com.isec.das.project2.repository.PessoaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaRepository pessoaRepository;

    public PessoaController(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size);

        Page<Pessoa> pageResult;

        if (email != null) {
            pageResult = pessoaRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else {
            pageResult = pessoaRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("page", pageResult.getNumber());
        response.put("size", pageResult.getSize());
        response.put("hasNext", pageResult.hasNext());
        response.put("items", pageResult.getContent());

        return ResponseEntity.ok(response);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Pessoa> obter(@PathVariable Long id) {
        Pessoa pessoa = pessoaRepository.findById(id).orElse(null);

        if (pessoa == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pessoa);

    }

    @PostMapping
    public ResponseEntity<Pessoa> criar(@RequestBody Pessoa pessoa) {
        Pessoa novo = pessoaRepository.save(pessoa);
        URI location = URI.create("/pessoas/" + novo.getId());
        return ResponseEntity.created(location).body(novo);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Pessoa> atualizar(
            @PathVariable Long id,
            @RequestBody Pessoa dados) {

        Pessoa pessoa = pessoaRepository.findById(id).orElse(null);

        if (pessoa == null) {
            return ResponseEntity.notFound().build();
        }

        if (dados.getNome() != null) {
            pessoa.setNome(dados.getNome());
        }
        if (dados.getEmail() != null) {
            pessoa.setEmail(dados.getEmail());
        }

        return ResponseEntity.ok(pessoaRepository.save(pessoa));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!pessoaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pessoaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
