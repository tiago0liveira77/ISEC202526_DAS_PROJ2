package com.isec.das.project2.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class CopiaLivro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @NonNull
    private Livro livro;

    @ManyToOne(optional = false)
    @NonNull
    private Biblioteca biblioteca;
}
