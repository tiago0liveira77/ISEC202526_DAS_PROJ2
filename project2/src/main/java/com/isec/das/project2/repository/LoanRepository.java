package com.isec.das.project2.repository; // Package declaration for repository interfaces

import com.isec.das.project2.model.Loan; // Import Loan model
import com.isec.das.project2.model.Person; // Import Person model
import org.springframework.data.jpa.repository.JpaRepository; // Import JpaRepository for standard CRUD operations
import org.springframework.stereotype.Repository; // Import Repository annotation

import java.util.List; // Import List interface

@Repository // Marks this interface as a Spring Data repository
public interface LoanRepository extends JpaRepository<Loan, Long> { // Interface definition extending JpaRepository for Loan entity with Long ID
    List<Loan> findByPersonAndReturnDateIsNull(Person person); // Custom query method to find active loans (return date is null) for a specific person
    List<Loan> findByPerson(Person person); // Custom query method to find all loans (history) for a specific person
}
