package com.isec.das.project2.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean done;

    @Lob
    private String result;

    private String metadata;
}
