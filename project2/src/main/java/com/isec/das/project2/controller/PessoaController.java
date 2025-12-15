package com.isec.das.project2.controller;

import com.isec.das.project2.model.Pessoa;
import com.isec.das.project2.repository.PessoaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaRepository pessoaRepository;

    public PessoaController(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @GetMapping
    public List<Pessoa> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        size = Math.min(size, 10);

        List<Pessoa> lista = pessoaRepository.findAll();
        int start = page * size;

        if (start >= lista.size()) return List.of();

        int end = Math.min(start + size, lista.size());
        return lista.subList(start, end);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Pessoa> obter(@PathVariable Long id) {
        return pessoaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Pessoa criar(@RequestBody Pessoa pessoa) {
        return pessoaRepository.save(pessoa);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Pessoa> atualizar(
            @PathVariable Long id,
            @RequestBody Pessoa dados) {

        return pessoaRepository.findById(id).map(pessoa -> {
            if (dados.getNome() != null) {
                pessoa.setNome(dados.getNome());
            }
            if (dados.getEmail() != null) {
                pessoa.setEmail(dados.getEmail());
            }
            return ResponseEntity.ok(pessoaRepository.save(pessoa));
        }).orElse(ResponseEntity.notFound().build());
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
