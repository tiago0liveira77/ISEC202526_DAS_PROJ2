package com.isec.das.project2.model; // Package declaration for the model classes

import jakarta.persistence.*; // Import JPA annotations for database mapping
import lombok.Data; // Import Lombok @Data annotation for getters, setters, toString, etc.

import java.util.List; // Import List interface for collections

@Entity // Marks this class as a JPA entity, meaning it maps to a database table
@Data // Lombok annotation to automatically generate getters, setters, equals, hashcode, and toString methods
public class Library { // Class definition for the Library entity
    @Id // Marks this field as the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the way the primary key is generated (auto-increment)
    private Long id; // Unique identifier for the library

    private String name; // Name of the library
    private String location; // Location of the library

    @OneToMany(mappedBy = "library") // Defines a one-to-many relationship with BookCopy. 'mappedBy' indicates the owning side is in BookCopy
    private List<BookCopy> bookCopies; // List of book copies available in this library

    @ManyToMany // Defines a many-to-many relationship with Person
    @JoinTable( // Configures the join table for the many-to-many relationship
        name = "library_registrations", // Name of the join table
        joinColumns = @JoinColumn(name = "library_id"), // Column in the join table that references this entity (Library)
        inverseJoinColumns = @JoinColumn(name = "person_id") // Column in the join table that references the other entity (Person)
    )
    private List<Person> registeredUsers; // List of users registered in this library
}
