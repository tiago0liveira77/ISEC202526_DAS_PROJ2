package com.isec.das.project2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Library {
    private String id;
    private String name;
    private String location;
    // Armazena a associação Many-to-Many com Person (membros registrados)
    private List<String> memberIds = new ArrayList<>();
}
