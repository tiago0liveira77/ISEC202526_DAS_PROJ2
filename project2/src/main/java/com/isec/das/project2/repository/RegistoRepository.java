package com.isec.das.project2.repository;

import com.isec.das.project2.model.Registo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistoRepository extends JpaRepository<Registo, Long> {

    Page<Registo> findByPessoaId(
            Long pessoaId,
            Pageable pageable
    );

    List<Registo> findByPessoaId(
            Long pessoaId
    );

    Page<Registo> findByBibliotecaId(
            Long bibliotecaId,
            Pageable pageable
    );
    Page<Registo> findByPessoaIdAndBibliotecaId(
            Long pessoaId,
            Long bibliotecaId,
            Pageable pageable
    );
    Optional<Registo> findByPessoaIdAndBibliotecaId(
            Long pessoaId,
            Long bibliotecaId
    );

}