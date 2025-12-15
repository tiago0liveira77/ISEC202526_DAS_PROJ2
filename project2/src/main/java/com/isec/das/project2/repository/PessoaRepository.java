package com.isec.das.project2.repository;

import com.isec.das.project2.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> { }