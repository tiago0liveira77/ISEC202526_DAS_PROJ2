package com.isec.das.project2.model; // Package declaration for the model classes

import jakarta.persistence.*; // Import JPA annotations for database mapping
import lombok.Data; // Import Lombok @Data annotation for getters, setters, toString, etc.

@Entity // Marks this class as a JPA entity, meaning it maps to a database table
@Data // Lombok annotation to automatically generate getters, setters, equals, hashcode, and toString methods
public class Book { // Class definition for the Book entity
    @Id // Marks this field as the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the way the primary key is generated (auto-increment)
    private Long id; // Unique identifier for the book

    private String title; // Title of the book
    private String author; // Author of the book
    private String isbn; // ISBN of the book

    @Lob // Marks this field as a Large Object (BLOB/CLOB) in the database, suitable for large text
    private String text; // Singleton sub-resource content, stores the full text of the book
}
