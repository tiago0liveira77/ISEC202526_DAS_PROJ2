package com.isec.das.project2.model;

import com.isec.das.project2.util.EstadoRegisto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
//Constrains usados para garantir que uma pessoa não pode ter múltiplos registos na mesma biblioteca
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"pessoa_id", "biblioteca_id"})
        }
)
public class Registo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Pessoa pessoa;

    @ManyToOne(optional = false)
    private Biblioteca biblioteca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRegisto estado;

    @Column(nullable = false)
    private LocalDate dataRegisto;
}
