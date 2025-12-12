package com.isec.das.project2.controller; // Package declaration for controller classes

import com.isec.das.project2.model.Library; // Import Library model
import com.isec.das.project2.service.LibraryService; // Import LibraryService
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
@RequestMapping("/libraries") // Maps requests starting with /libraries to this controller
public class LibraryController { // Controller class for Library resources

    @Autowired // Injects the LibraryService bean
    private LibraryService libraryService; // Service for library operations

    @GetMapping // Maps GET requests to this method
    public ResponseEntity<List<Map<String, Object>>> getAllLibraries( // Method to get all libraries
            @RequestParam(required = false) String location, // Optional query parameter for location filtering
            @RequestParam(defaultValue = "0") int page, // Query parameter for page number, default 0
            @RequestParam(defaultValue = "10") int size, // Query parameter for page size, default 10
            @RequestParam(required = false) String fields) { // Optional query parameter for field masking

        Pageable pageable = PageRequest.of(page, size); // Create Pageable object
        Page<Library> libraries = libraryService.findAll(location, pageable); // Get paginated libraries from service

        List<Map<String, Object>> response = libraries.getContent().stream() // Stream the content of the page
                .map(library -> FieldMaskUtil.applyFieldMask(library, fields)) // Apply field mask to each library
                .collect(Collectors.toList()); // Collect results into a list

        return ResponseEntity.ok(response); // Return the response with 200 OK status
    }

    @GetMapping("/{id}") // Maps GET requests with ID to this method
    public ResponseEntity<Map<String, Object>> getLibraryById( // Method to get a library by ID
            @PathVariable Long id, // Path variable for library ID
            @RequestParam(required = false) String fields) { // Optional query parameter for field masking
        return libraryService.findById(id) // Find library by ID
                .map(library -> ResponseEntity.ok(FieldMaskUtil.applyFieldMask(library, fields))) // If found, apply
                                                                                                  // mask and return 200
                                                                                                  // OK
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    @PostMapping // Maps POST requests to this method
    public ResponseEntity<Library> createLibrary(@RequestBody Library library) { // Method to create a new library
        return ResponseEntity.ok(libraryService.save(library)); // Save library and return 200 OK
    }

    @PostMapping("/{id}/registrations") // Maps POST requests for registrations to this method
    public ResponseEntity<Void> registerUser(@PathVariable Long id, @RequestParam Long userId) { // Method to register a
                                                                                                 // user
        libraryService.registerUser(id, userId); // Register user in library
        return ResponseEntity.ok().build(); // Return 200 OK
    }

    @DeleteMapping("/{id}/registrations") // Maps DELETE requests for registrations to this method
    public ResponseEntity<Void> unregisterUser(@PathVariable Long id, @RequestParam Long userId) { // Method to
                                                                                                   // unregister a user
        libraryService.unregisterUser(id, userId); // Unregister user from library
        return ResponseEntity.ok().build(); // Return 200 OK
    }

    @GetMapping("/{id}/books") // Maps GET requests for library books to this method
    public ResponseEntity<List<Map<String, Object>>> getLibraryBooks( // Method to get books of a library
            @PathVariable Long id, // Path variable for library ID
            @RequestParam(defaultValue = "0") int page, // Query parameter for page number
            @RequestParam(defaultValue = "10") int size, // Query parameter for page size
            @RequestParam(required = false) String fields) { // Optional query parameter for field masking

        Pageable pageable = PageRequest.of(page, size); // Create Pageable object
        Page<com.isec.das.project2.model.BookCopy> bookCopies = libraryService.getLibraryBooks(id, pageable); // Get
                                                                                                              // paginated
                                                                                                              // book
                                                                                                              // copies

        List<Map<String, Object>> response = bookCopies.getContent().stream() // Stream content
                .map(copy -> FieldMaskUtil.applyFieldMask(copy, fields)) // Apply mask
                .collect(Collectors.toList()); // Collect to list

        return ResponseEntity.ok(response); // Return response
    }
}
