package com.isec.das.project2.model;

import com.isec.das.project2.util.EstadoRegisto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@RequiredArgsConstructor
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
    @NonNull
    private Pessoa pessoa;

    @ManyToOne(optional = false)
    @NonNull
    private Biblioteca biblioteca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private EstadoRegisto estado;

    @Column(nullable = false)
    @NonNull
    private LocalDate dataRegisto;
}
