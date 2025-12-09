package com.isec.das.project2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private String id;
    private String title;
    private String author;
    private String libraryId;
    private BookOverview overview = new BookOverview(); // Composição do Singleton
}