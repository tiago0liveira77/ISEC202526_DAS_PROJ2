package com.isec.das.project2.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
public class CopiaLivro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Livro livro;

    @ManyToOne(optional = false)
    private Biblioteca biblioteca;

    @Builder
    public CopiaLivro(Livro livro, Biblioteca biblioteca) {
        this.livro = livro;
        this.biblioteca = biblioteca;
    }
}
