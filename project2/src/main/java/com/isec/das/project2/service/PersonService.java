package com.isec.das.project2.service; // Package declaration for service classes

import com.isec.das.project2.model.BookCopy; // Import BookCopy model
import com.isec.das.project2.model.Loan; // Import Loan model
import com.isec.das.project2.model.Person; // Import Person model
import com.isec.das.project2.repository.BookCopyRepository; // Import BookCopyRepository
import com.isec.das.project2.repository.LoanRepository; // Import LoanRepository
import com.isec.das.project2.repository.PersonRepository; // Import PersonRepository
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired annotation for dependency injection
import org.springframework.data.domain.Page; // Import Page for pagination support
import org.springframework.data.domain.Pageable; // Import Pageable for pagination information
import org.springframework.stereotype.Service; // Import Service annotation
import org.springframework.transaction.annotation.Transactional; // Import Transactional annotation for transaction management

import java.time.LocalDateTime; // Import LocalDateTime for handling date and time
import java.util.List; // Import List interface
import java.util.Optional; // Import Optional for handling potential null values

@Service // Marks this class as a Spring service
public class PersonService { // Service class for Person operations

    @Autowired // Injects the PersonRepository bean
    private PersonRepository personRepository; // Repository for person data access

    @Autowired // Injects the LoanRepository bean
    private LoanRepository loanRepository; // Repository for loan data access

    @Autowired // Injects the BookCopyRepository bean
    private BookCopyRepository bookCopyRepository; // Repository for book copy data access

    public Page<Person> findAll(String name, Pageable pageable) { // Method to find all people, optionally filtered by
                                                                  // name, with pagination
        if (name != null && !name.isEmpty()) { // Check if name filter is provided
            return personRepository.findByNameContaining(name, pageable); // Return filtered people
        }
        return personRepository.findAll(pageable); // Return all people if no filter is provided
    }

    public Optional<Person> findById(Long id) { // Method to find a person by their ID
        return personRepository.findById(id); // Returns an Optional containing the person if found
    }

    public Person save(Person person) { // Method to save or update a person
        return personRepository.save(person); // Saves the person to the database
    }

    public void deleteById(Long id) {
        personRepository.deleteById(id);
    }

    @Autowired
    private com.isec.das.project2.repository.RegistrationRepository registrationRepository;

    @Transactional // Ensures this method runs within a database transaction
    public Loan loanBook(Long personId, Long bookCopyId) { // Method to loan a book to a person
        Person person = personRepository.findById(personId).orElseThrow(() -> new RuntimeException("Person not found"));
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new RuntimeException("BookCopy not found"));

        // Check registration using repository
        if (registrationRepository.findByLibraryIdAndPersonId(bookCopy.getLibrary().getId(), personId).isEmpty()) {
            throw new RuntimeException("Person is not registered in the library where the book is located");
        }

        Loan loan = new Loan(); // Create a new Loan object
        loan.setPerson(person); // Set the person for the loan
        loan.setBookCopy(bookCopy); // Set the book copy for the loan
        loan.setLoanDate(LocalDateTime.now()); // Set the loan date to current time

        return loanRepository.save(loan); // Save the loan to the database
    }

    @Transactional // Ensures this method runs within a database transaction
    public void returnBook(Long loanId) { // Method to return a loaned book
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found")); // Find
                                                                                                               // loan
                                                                                                               // or
                                                                                                               // throw
                                                                                                               // exception
        loan.setReturnDate(LocalDateTime.now()); // Set the return date to current time
        loanRepository.save(loan); // Save the updated loan to the database
    }

    public List<Loan> getCurrentLoans(Long personId) { // Method to get current active loans for a person
        Person person = personRepository.findById(personId).orElseThrow(() -> new RuntimeException("Person not found")); // Find
                                                                                                                         // person
                                                                                                                         // or
                                                                                                                         // throw
                                                                                                                         // exception
        return loanRepository.findByPersonAndReturnDateIsNull(person); // Return list of active loans
    }

    public List<Loan> getLoanHistory(Long personId) { // Method to get loan history for a person
        Person person = personRepository.findById(personId).orElseThrow(() -> new RuntimeException("Person not found")); // Find
                                                                                                                         // person
                                                                                                                         // or
                                                                                                                         // throw
                                                                                                                         // exception
        return loanRepository.findByPerson(person); // Return list of all loans (history)
    }
}
