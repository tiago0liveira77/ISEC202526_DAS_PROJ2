package com.isec.das.project2.model;

import com.isec.das.project2.util.EstadoEmprestimo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Pessoa pessoa;

    @ManyToOne(optional = false)
    private CopiaLivro copiaLivro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEmprestimo estado;

    @Column(nullable = false)
    private LocalDate dataEmprestimo;

    private LocalDate dataDevolucao; // null enquanto ativo

    @Builder
    public Emprestimo(Pessoa pessoa, CopiaLivro copiaLivro, EstadoEmprestimo estado, LocalDate dataEmprestimo) {
        this.pessoa = pessoa;
        this.copiaLivro = copiaLivro;
        this.estado = estado;
        this.dataEmprestimo = dataEmprestimo;
    }
}
