package com.isec.das.project2.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean done;

    @Builder
    public Operation(boolean done) {
        this.done = done;
    }

    @Lob
    private String result;

    private String metadata;
}
