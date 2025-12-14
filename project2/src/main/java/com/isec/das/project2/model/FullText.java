package com.isec.das.project2.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FullText {

    @Id
    private Long id;  // mesmo ID do livro

    @OneToOne
    @MapsId
    private Livro livro;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String texto;
}
