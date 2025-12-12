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

    public Page<Library> findAll(String location, Pageable pageable) { // Method to find all libraries, optionally filtered by location, with pagination
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

    @Transactional // Ensures this method runs within a database transaction
    public void registerUser(Long libraryId, Long userId) { // Method to register a user in a library
        Library library = libraryRepository.findById(libraryId).orElseThrow(() -> new RuntimeException("Library not found")); // Find library or throw exception
        Person person = personRepository.findById(userId).orElseThrow(() -> new RuntimeException("Person not found")); // Find person or throw exception
        
        library.getRegisteredUsers().add(person); // Add person to the library's registered users list
        person.getLibraries().add(library); // Add library to the person's libraries list (maintain bidirectional relationship)
        
        libraryRepository.save(library); // Save the updated library entity
        personRepository.save(person); // Save the updated person entity
    }

    @Transactional // Ensures this method runs within a database transaction
    public void unregisterUser(Long libraryId, Long userId) { // Method to unregister a user from a library
        Library library = libraryRepository.findById(libraryId).orElseThrow(() -> new RuntimeException("Library not found")); // Find library or throw exception
        Person person = personRepository.findById(userId).orElseThrow(() -> new RuntimeException("Person not found")); // Find person or throw exception

        library.getRegisteredUsers().remove(person); // Remove person from the library's registered users list
        person.getLibraries().remove(library); // Remove library from the person's libraries list

        libraryRepository.save(library); // Save the updated library entity
        personRepository.save(person); // Save the updated person entity
    }
}
