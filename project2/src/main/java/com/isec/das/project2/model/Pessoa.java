package com.isec.das.project2.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

}
