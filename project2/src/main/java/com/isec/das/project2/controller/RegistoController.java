package com.isec.das.project2.controller;

import com.isec.das.project2.model.*;
import com.isec.das.project2.repository.*;
import com.isec.das.project2.util.EstadoRegisto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/registos")
public class RegistoController {

    private final RegistoRepository registoRepository;
    private final PessoaRepository pessoaRepository;
    private final BibliotecaRepository bibliotecaRepository;

    public RegistoController(
            RegistoRepository registoRepository,
            PessoaRepository pessoaRepository,
            BibliotecaRepository bibliotecaRepository) {
        this.registoRepository = registoRepository;
        this.pessoaRepository = pessoaRepository;
        this.bibliotecaRepository = bibliotecaRepository;
    }

    @PostMapping
    public ResponseEntity<Registo> criar(
            @RequestParam Long pessoaId,
            @RequestParam Long bibliotecaId) {

        Pessoa pessoa = pessoaRepository.findById(pessoaId).orElse(null);
        Biblioteca biblioteca = bibliotecaRepository.findById(bibliotecaId).orElse(null);

        if (pessoa == null || biblioteca == null) {
            return ResponseEntity.badRequest().build();
        }

        Registo novo = registoRepository.save(Registo.builder()
                .pessoa(pessoa)
                .biblioteca(biblioteca)
                .estado(EstadoRegisto.ATIVO)
                .dataRegisto(LocalDate.now())
                .build());

        URI location = URI.create("/registos/" + novo.getId());
        return ResponseEntity.created(location).body(novo);
    }

    @GetMapping
    public List<Registo> listar(
            @RequestParam(required = false) Long pessoaId,
            @RequestParam(required = false) Long bibliotecaId) {

        if (pessoaId != null) {
            return registoRepository.findByPessoaId(pessoaId);
        }
        if (bibliotecaId != null) {
            return registoRepository.findByBibliotecaId(bibliotecaId);
        }
        return registoRepository.findAll();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Registo> cancelar(@PathVariable Long id) {
        return registoRepository.findById(id).map(registo -> {
            registo.setEstado(EstadoRegisto.CANCELADO);
            return ResponseEntity.ok(registoRepository.save(registo));
        }).orElse(ResponseEntity.notFound().build());
    }
}
