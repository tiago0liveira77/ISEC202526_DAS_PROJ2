package com.isec.das.project2.model; // Package declaration for the model classes

import jakarta.persistence.*; // Import JPA annotations for database mapping
import lombok.Data; // Import Lombok @Data annotation for getters, setters, toString, etc.

import java.time.LocalDateTime; // Import LocalDateTime for handling date and time

@Entity // Marks this class as a JPA entity, meaning it maps to a database table
@Data // Lombok annotation to automatically generate getters, setters, equals, hashcode, and toString methods
public class Loan { // Class definition for the Loan entity
    @Id // Marks this field as the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the way the primary key is generated (auto-increment)
    private Long id; // Unique identifier for the loan

    @ManyToOne // Defines a many-to-one relationship with Person
    @JoinColumn(name = "person_id") // Specifies the foreign key column name in the database table
    private Person person; // The person who made the loan

    @ManyToOne // Defines a many-to-one relationship with BookCopy
    @JoinColumn(name = "book_copy_id") // Specifies the foreign key column name in the database table
    private BookCopy bookCopy; // The book copy that was loaned

    private LocalDateTime loanDate; // The date and time when the loan was made
    private LocalDateTime returnDate; // The date and time when the book was returned (null if not returned yet)
}
