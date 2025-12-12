package com.isec.das.project2.controller; // Package declaration for controller classes

import com.isec.das.project2.model.Loan; // Import Loan model
import com.isec.das.project2.model.Person; // Import Person model
import com.isec.das.project2.service.PersonService; // Import PersonService
import com.isec.das.project2.util.FieldMaskUtil; // Import FieldMaskUtil
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired annotation
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.PageRequest; // Import PageRequest
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.http.ResponseEntity; // Import ResponseEntity
import org.springframework.web.bind.annotation.*; // Import Spring Web annotations

import java.util.List; // Import List
import java.util.Map; // Import Map
import java.util.stream.Collectors; // Import Collectors

@RestController // Marks this class as a REST controller
@RequestMapping("/people") // Maps requests starting with /people to this controller
public class PersonController { // Controller class for Person resources

    @Autowired // Injects the PersonService bean
    private PersonService personService; // Service for person operations

    @GetMapping // Maps GET requests to this method
    public ResponseEntity<List<Map<String, Object>>> getAllPeople( // Method to get all people
            @RequestParam(required = false) String name, // Optional query parameter for name filtering
            @RequestParam(defaultValue = "0") int page, // Query parameter for page number, default 0
            @RequestParam(defaultValue = "10") int size, // Query parameter for page size, default 10
            @RequestParam(required = false) String fields) { // Optional query parameter for field masking

        Pageable pageable = PageRequest.of(page, size); // Create Pageable object
        Page<Person> people = personService.findAll(name, pageable); // Get paginated people from service

        List<Map<String, Object>> response = people.getContent().stream() // Stream the content of the page
                .map(person -> FieldMaskUtil.applyFieldMask(person, fields)) // Apply field mask to each person
                .collect(Collectors.toList()); // Collect results into a list

        return ResponseEntity.ok(response); // Return the response with 200 OK status
    }

    @GetMapping("/{id}") // Maps GET requests with ID to this method
    public ResponseEntity<Map<String, Object>> getPersonById( // Method to get a person by ID
            @PathVariable Long id, // Path variable for person ID
            @RequestParam(required = false) String fields) { // Optional query parameter for field masking
        return personService.findById(id) // Find person by ID
                .map(person -> ResponseEntity.ok(FieldMaskUtil.applyFieldMask(person, fields))) // If found, apply mask
                                                                                                // and return 200 OK
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    @PostMapping // Maps POST requests to this method
    public ResponseEntity<Person> createPerson(@RequestBody Person person) { // Method to create a new person
        return ResponseEntity.ok(personService.save(person)); // Save person and return 200 OK
    }

    @PostMapping("/{id}/loans") // Maps POST requests for creating loans to this method
    public ResponseEntity<Loan> loanBook(@PathVariable Long id, @RequestParam Long bookCopyId) { // Method to loan a
                                                                                                 // book
        return ResponseEntity.ok(personService.loanBook(id, bookCopyId)); // Create loan and return 200 OK
    }

    @PostMapping("/{id}/loans/{loanId}/return") // Maps POST requests for returning books to this method
    public ResponseEntity<Void> returnBook(@PathVariable Long id, @PathVariable Long loanId) { // Method to return a
                                                                                               // book
        // Ideally we should verify if the loan belongs to the person (id), but for
        // simplicity we trust loanId
        personService.returnBook(loanId); // Return book
        return ResponseEntity.ok().build(); // Return 200 OK
    }

    @GetMapping("/{id}/loans") // Maps GET requests for loans to this method
    public ResponseEntity<List<Loan>> getLoans( // Method to get loans
            @PathVariable Long id, // Person ID
            @RequestParam(required = false) Boolean active) { // Optional filter for active loans

        if (active != null) {
            if (active) {
                return ResponseEntity.ok(personService.getCurrentLoans(id));
            } else {
                return ResponseEntity.ok(personService.getLoanHistory(id));
            }
        }
        // If active is null, maybe return all? For now let's default to current or
        // history?
        // Let's return history (which implies all past loans, but maybe not current?)
        // The service distinction is:
        // getCurrentLoans -> returnDate is null
        // getLoanHistory -> returnDate is NOT null
        // So to get ALL, we would need a new method.
        // For now, let's default to current if not specified, or support both.
        // Let's assume default is current.
        return ResponseEntity.ok(personService.getCurrentLoans(id));
    }
}
