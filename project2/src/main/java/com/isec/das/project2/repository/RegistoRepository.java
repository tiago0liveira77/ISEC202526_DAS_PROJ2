package com.isec.das.project2.repository;

import com.isec.das.project2.model.Registo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistoRepository extends JpaRepository<Registo, Long> {

    List<Registo> findByPessoaId(Long pessoaId);
    List<Registo> findByBibliotecaId(Long bibliotecaId);
    List<Registo> findByPessoaIdAndEstado(Long pessoaId, Enum estado);
}