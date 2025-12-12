package com.isec.das.project2.repository; // Package declaration for repository interfaces

import com.isec.das.project2.model.BookCopy; // Import BookCopy model
import org.springframework.data.jpa.repository.JpaRepository; // Import JpaRepository for standard CRUD operations
import org.springframework.stereotype.Repository; // Import Repository annotation

@Repository // Marks this interface as a Spring Data repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> { // Interface definition extending
                                                                            // JpaRepository for BookCopy entity with
                                                                            // Long ID
    org.springframework.data.domain.Page<BookCopy> findByLibrary(com.isec.das.project2.model.Library library,
            org.springframework.data.domain.Pageable pageable);
}
