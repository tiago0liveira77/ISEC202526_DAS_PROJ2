package com.isec.das.project2.repository; // Package declaration for repository interfaces

import com.isec.das.project2.model.Book; // Import Book model
import org.springframework.data.domain.Page; // Import Page for pagination support
import org.springframework.data.domain.Pageable; // Import Pageable for pagination information
import org.springframework.data.jpa.repository.JpaRepository; // Import JpaRepository for standard CRUD operations
import org.springframework.stereotype.Repository; // Import Repository annotation

@Repository // Marks this interface as a Spring Data repository
public interface BookRepository extends JpaRepository<Book, Long> { // Interface definition extending JpaRepository for Book entity with Long ID
    Page<Book> findByAuthorContaining(String author, Pageable pageable); // Custom query method to find books by author containing a string, with pagination
}
