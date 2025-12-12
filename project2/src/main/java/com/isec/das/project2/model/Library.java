package com.isec.das.project2.model; // Package declaration for the model classes

import jakarta.persistence.*; // Import JPA annotations for database mapping
import lombok.Data; // Import Lombok @Data annotation for getters, setters, toString, etc.

import java.util.List; // Import List interface for collections

@Entity // Marks this class as a JPA entity, meaning it maps to a database table
@Data // Lombok annotation to automatically generate getters, setters, equals,
      // hashcode, and toString methods
public class Library { // Class definition for the Library entity
    @Id // Marks this field as the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the way the primary key is generated
                                                        // (auto-increment)
    private Long id; // Unique identifier for the library

    private String name; // Name of the library
    private String location; // Location of the library

    @OneToMany(mappedBy = "library") // Defines a one-to-many relationship with BookCopy. 'mappedBy' indicates the
                                     // owning side is in BookCopy
    private List<BookCopy> bookCopies; // List of book copies available in this library

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations; // List of registrations (Association Resource)
}
