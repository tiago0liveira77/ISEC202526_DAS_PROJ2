package com.isec.das.project2.model; // Package declaration for the model classes

import jakarta.persistence.*; // Import JPA annotations for database mapping
import lombok.Data; // Import Lombok @Data annotation for getters, setters, toString, etc.

import java.util.List; // Import List interface for collections

@Entity // Marks this class as a JPA entity, meaning it maps to a database table
@Data // Lombok annotation to automatically generate getters, setters, equals,
      // hashcode, and toString methods
public class Person { // Class definition for the Person entity
    @Id // Marks this field as the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the way the primary key is generated
                                                        // (auto-increment)
    private Long id; // Unique identifier for the person

    private String name; // Name of the person
    private String email; // Email of the person

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations; // List of registrations (Association Resource)
}
