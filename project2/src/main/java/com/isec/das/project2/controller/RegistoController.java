package com.isec.das.project2.controller;

import com.isec.das.project2.model.*;
import com.isec.das.project2.repository.*;
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

import static com.isec.das.project2.util.FieldMasks.aplicarFieldMask;

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

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) Long pessoaId,
            @RequestParam(required = false) Long bibliotecaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size);

        Page<Registo> pageResult;
        if (pessoaId != null && bibliotecaId != null) {
            pageResult = registoRepository.findByPessoaIdAndBibliotecaId(pessoaId, bibliotecaId, pageable);
        }
        else if (pessoaId != null) {
            pageResult = registoRepository.findByPessoaId(pessoaId, pageable);
        } else if (bibliotecaId != null) {
            pageResult = registoRepository.findByBibliotecaId(bibliotecaId, pageable);
        } else {
            pageResult = registoRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("page", pageResult.getNumber());
        response.put("size", pageResult.getSize());
        response.put("hasNext", pageResult.hasNext());
        response.put("items", pageResult.getContent());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> obter(@PathVariable Long id,
                                   @RequestParam(value = "fields", defaultValue = "*") Set<String> fields){

        Optional<Registo> optRegisto = registoRepository.findById(id);

        if(optRegisto.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = aplicarFieldMask(optRegisto.get(), fields);

        return ResponseEntity.ok(response);
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

        Registo registo = registoRepository.findByPessoaIdAndBibliotecaId(pessoaId, bibliotecaId).orElse(null);

        if(registo != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            Registo novo = registoRepository.save(Registo.builder()
                    .pessoa(pessoa)
                    .biblioteca(biblioteca)
                    .estado(EstadoRegisto.ATIVO)
                    .dataRegisto(LocalDate.now())
                    .build());

            URI location = URI.create("/registos/" + novo.getId());
            return ResponseEntity.created(location).body(novo);
        }
    }



    @PatchMapping("/{id}:cancelar")
    public ResponseEntity<Registo> cancelar(@PathVariable Long id) {

        Registo registo = registoRepository.findById(id).orElse(null);

        if (registo == null) {
            return ResponseEntity.notFound().build();
        }

        if (!registo.getEstado().equals(EstadoRegisto.ATIVO)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        registo.setEstado(EstadoRegisto.CANCELADO);
        return ResponseEntity.ok(registoRepository.save(registo));

    }

    @PatchMapping("/{id}:ativar")
    public ResponseEntity<Registo> ativar(@PathVariable Long id) {

        Registo registo = registoRepository.findById(id).orElse(null);

        if (registo == null) {
            return ResponseEntity.notFound().build();
        }

        if (!registo.getEstado().equals(EstadoRegisto.CANCELADO)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        registo.setEstado(EstadoRegisto.ATIVO);
        return ResponseEntity.ok(registoRepository.save(registo));

    }
}
