package com.isec.das.project2.repository;

import com.isec.das.project2.model.CopiaLivro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CopiaLivroRepository extends JpaRepository<CopiaLivro, Long> {

    Page<CopiaLivro> findByBibliotecaId(
            Long bibliotecaId,
            Pageable pageable
    );
}
