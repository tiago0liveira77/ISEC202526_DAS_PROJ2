package com.isec.das.project2.repository;

import com.isec.das.project2.model.Emprestimo;
import com.isec.das.project2.util.EstadoEmprestimo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    Page<Emprestimo> findByPessoaId(
            Long pessoaId,
            Pageable pageable
    );

    Page<Emprestimo> findByPessoaIdAndEstado(
            Long pessoaId,
            EstadoEmprestimo estado,
            Pageable pageable
    );


    List<Emprestimo> findByCopiaLivroId(Long copiaId);
}
