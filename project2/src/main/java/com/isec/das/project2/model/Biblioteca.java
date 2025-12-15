package com.isec.das.project2.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
public class Biblioteca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String localizacao;

    @Builder
    public Biblioteca(String localizacao, String nome) {
        this.localizacao = localizacao;
        this.nome = nome;
    }
}
