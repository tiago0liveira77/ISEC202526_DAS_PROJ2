package com.isec.das.project2.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;
}
