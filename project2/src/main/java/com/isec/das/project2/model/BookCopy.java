package com.isec.das.project2.model; // Package declaration for the model classes

import jakarta.persistence.*; // Import JPA annotations for database mapping
import lombok.Data; // Import Lombok @Data annotation for getters, setters, toString, etc.

@Entity // Marks this class as a JPA entity, meaning it maps to a database table
@Data // Lombok annotation to automatically generate getters, setters, equals, hashcode, and toString methods
public class BookCopy { // Class definition for the BookCopy entity
    @Id // Marks this field as the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the way the primary key is generated (auto-increment)
    private Long id; // Unique identifier for the book copy

    @ManyToOne // Defines a many-to-one relationship with Book
    @JoinColumn(name = "book_id") // Specifies the foreign key column name in the database table
    private Book book; // The book this copy belongs to

    @ManyToOne // Defines a many-to-one relationship with Library
    @JoinColumn(name = "library_id") // Specifies the foreign key column name in the database table
    private Library library; // The library where this copy is located
}
