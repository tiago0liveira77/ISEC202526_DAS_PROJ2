package com.isec.das.project2.service; // Package declaration for service classes

import com.isec.das.project2.model.Library; // Import Library model
import com.isec.das.project2.model.Person; // Import Person model
import com.isec.das.project2.repository.LibraryRepository; // Import LibraryRepository
import com.isec.das.project2.repository.PersonRepository; // Import PersonRepository
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired annotation for dependency injection
import org.springframework.data.domain.Page; // Import Page for pagination support
import org.springframework.data.domain.Pageable; // Import Pageable for pagination information
import org.springframework.stereotype.Service; // Import Service annotation
import org.springframework.transaction.annotation.Transactional; // Import Transactional annotation for transaction management

import java.util.Optional; // Import Optional for handling potential null values

@Service // Marks this class as a Spring service
public class LibraryService { // Service class for Library operations

    @Autowired // Injects the LibraryRepository bean
    private LibraryRepository libraryRepository; // Repository for library data access

    @Autowired // Injects the PersonRepository bean
    private PersonRepository personRepository; // Repository for person data access

    @Autowired
    private com.isec.das.project2.repository.BookCopyRepository bookCopyRepository;

    public Page<Library> findAll(String location, Pageable pageable) { // Method to find all libraries, optionally
                                                                       // filtered by location, with pagination
        if (location != null && !location.isEmpty()) { // Check if location filter is provided
            return libraryRepository.findByLocationContaining(location, pageable); // Return filtered libraries
        }
        return libraryRepository.findAll(pageable); // Return all libraries if no filter is provided
    }

    public Optional<Library> findById(Long id) { // Method to find a library by its ID
        return libraryRepository.findById(id); // Returns an Optional containing the library if found
    }

    public Library save(Library library) { // Method to save or update a library
        return libraryRepository.save(library); // Saves the library to the database
    }

    public void deleteById(Long id) {
        libraryRepository.deleteById(id);
    }

    @Autowired
    private com.isec.das.project2.repository.RegistrationRepository registrationRepository;

    @Transactional
    public void registerUser(Long libraryId, Long userId) {
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new RuntimeException("Library not found"));
        Person person = personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        if (registrationRepository.findByLibraryIdAndPersonId(libraryId, userId).isPresent()) {
            throw new RuntimeException("User already registered in this library");
        }

        com.isec.das.project2.model.Registration registration = new com.isec.das.project2.model.Registration();
        registration.setLibrary(library);
        registration.setPerson(person);
        registrationRepository.save(registration);
    }

    @Transactional
    public void unregisterUser(Long libraryId, Long userId) {
        com.isec.das.project2.model.Registration registration = registrationRepository
                .findByLibraryIdAndPersonId(libraryId, userId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registrationRepository.delete(registration);
    }

    public Page<com.isec.das.project2.model.BookCopy> getLibraryBooks(Long libraryId, Pageable pageable) { // Method to
                                                                                                           // get books
                                                                                                           // (copies)
                                                                                                           // of a
                                                                                                           // library
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new RuntimeException("Library not found")); // Find library or throw exception
        // Assuming we want to return BookCopies which represent the books in the
        // library
        // We need a repository method for this or filter the list.
        // Better to use a repository method in BookCopyRepository.
        // For now, let's assume we inject BookCopyRepository here.
        return bookCopyRepository.findByLibrary(library, pageable);
    }
}
