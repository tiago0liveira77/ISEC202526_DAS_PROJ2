package com.isec.das.project2.repository;

import com.isec.das.project2.model.Biblioteca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliotecaRepository extends JpaRepository<Biblioteca, Long> {

    Page<Biblioteca> findByLocalizacaoContainingIgnoreCase(
            String localizacao,
            Pageable pageable
    );

}
