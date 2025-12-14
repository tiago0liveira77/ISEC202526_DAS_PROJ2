package com.isec.das.project2.repository;

import com.isec.das.project2.model.Emprestimo;
import com.isec.das.project2.util.EstadoEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    List<Emprestimo> findByPessoaId(Long pessoaId);

    List<Emprestimo> findByPessoaIdAndEstado(Long pessoaId, EstadoEmprestimo estado);

    List<Emprestimo> findByCopiaLivroId(Long copiaId);
}
