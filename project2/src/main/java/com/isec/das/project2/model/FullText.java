package com.isec.das.project2.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class FullText {

    @Id
    private Long id;  // mesmo ID do livro

    @OneToOne
    @MapsId
    private Livro livro;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String texto;

    @Builder
    public FullText(Livro livro, String texto) {
        this.livro = livro;
        this.texto = texto;
    }
}
