package com.isec.das.project2.model;

import com.isec.das.project2.util.EstadoRegisto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
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

    @Builder
    public Registo(Pessoa pessoa, Biblioteca biblioteca, EstadoRegisto estado, LocalDate dataRegisto) {
        this.pessoa = pessoa;
        this.biblioteca = biblioteca;
        this.estado = estado;
        this.dataRegisto = dataRegisto;
    }
}
