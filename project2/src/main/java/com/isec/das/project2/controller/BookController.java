package com.isec.das.project2.controller; // Package declaration for controller classes

import com.isec.das.project2.model.Book; // Import Book model
import com.isec.das.project2.service.BookService; // Import BookService
import com.isec.das.project2.util.FieldMaskUtil; // Import FieldMaskUtil
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired annotation
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.PageRequest; // Import PageRequest
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.http.ResponseEntity; // Import ResponseEntity
import org.springframework.web.bind.annotation.*; // Import Spring Web annotations

import java.util.List; // Import List
import java.util.Map; // Import Map
import java.util.concurrent.CompletableFuture; // Import CompletableFuture
import java.util.stream.Collectors; // Import Collectors

@RestController // Marks this class as a REST controller
@RequestMapping("/books") // Maps requests starting with /books to this controller
public class BookController { // Controller class for Book resources

    @Autowired // Injects the BookService bean
    private BookService bookService; // Service for book operations

    @GetMapping // Maps GET requests to this method
    public ResponseEntity<List<Map<String, Object>>> getAllBooks( // Method to get all books
            @RequestParam(required = false) String author, // Optional query parameter for author filtering
            @RequestParam(defaultValue = "0") int page, // Query parameter for page number, default 0
            @RequestParam(defaultValue = "10") int size, // Query parameter for page size, default 10
            @RequestParam(required = false) String fields) { // Optional query parameter for field masking
        
        Pageable pageable = PageRequest.of(page, size); // Create Pageable object
        Page<Book> books = bookService.findAll(author, pageable); // Get paginated books from service
        
        List<Map<String, Object>> response = books.getContent().stream() // Stream the content of the page
                .map(book -> FieldMaskUtil.applyFieldMask(book, fields)) // Apply field mask to each book
                .collect(Collectors.toList()); // Collect results into a list
        
        return ResponseEntity.ok(response); // Return the response with 200 OK status
    }

    @GetMapping("/{id}") // Maps GET requests with ID to this method
    public ResponseEntity<Map<String, Object>> getBookById( // Method to get a book by ID
            @PathVariable Long id, // Path variable for book ID
            @RequestParam(required = false) String fields) { // Optional query parameter for field masking
        return bookService.findById(id) // Find book by ID
                .map(book -> ResponseEntity.ok(FieldMaskUtil.applyFieldMask(book, fields))) // If found, apply mask and return 200 OK
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    @PostMapping // Maps POST requests to this method
    public ResponseEntity<Book> createBook(@RequestBody Book book) { // Method to create a new book
        return ResponseEntity.ok(bookService.save(book)); // Save book and return 200 OK
    }

    @PostMapping("/copies/{id}/move") // Maps POST requests for moving copies to this method
    public ResponseEntity<Void> moveBookCopy(@PathVariable Long id, @RequestParam Long targetLibraryId) { // Method to move a book copy
        bookService.moveBookCopy(id, targetLibraryId); // Move book copy to target library
        return ResponseEntity.ok().build(); // Return 200 OK
    }

    // Singleton sub-resource for text
    @GetMapping("/{id}/text") // Maps GET requests for book text to this method
    public ResponseEntity<String> getBookText(@PathVariable Long id) { // Method to get book text
        return bookService.findById(id) // Find book by ID
                .map(book -> ResponseEntity.ok(book.getText() != null ? book.getText() : "")) // If found, return text or empty string
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    // LRO Search
    @PostMapping("/search") // Maps POST requests for search to this method
    public CompletableFuture<ResponseEntity<String>> searchBooks(@RequestParam String query) { // Method to search books asynchronously
        return bookService.searchFullText(query) // Call async search method
                .thenApply(ResponseEntity::ok); // Wrap result in ResponseEntity when complete
    }
}
