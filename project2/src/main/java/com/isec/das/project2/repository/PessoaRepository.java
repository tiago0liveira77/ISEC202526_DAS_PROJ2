package com.isec.das.project2.repository;

import com.isec.das.project2.model.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    Page<Pessoa> findByEmailContainingIgnoreCase(
            String email,
            Pageable pageable
    );

}