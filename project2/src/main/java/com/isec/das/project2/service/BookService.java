package com.isec.das.project2.service; // Package declaration for service classes

import com.isec.das.project2.model.Book; // Import Book model
import com.isec.das.project2.model.BookCopy; // Import BookCopy model
import com.isec.das.project2.model.Library; // Import Library model
import com.isec.das.project2.repository.BookCopyRepository; // Import BookCopyRepository
import com.isec.das.project2.repository.BookRepository; // Import BookRepository
import com.isec.das.project2.repository.LibraryRepository; // Import LibraryRepository
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired annotation for dependency injection
import org.springframework.data.domain.Page; // Import Page for pagination support
import org.springframework.data.domain.Pageable; // Import Pageable for pagination information
import org.springframework.stereotype.Service; // Import Service annotation
import org.springframework.transaction.annotation.Transactional; // Import Transactional annotation for transaction management

import java.util.Optional; // Import Optional for handling potential null values
import java.util.concurrent.CompletableFuture; // Import CompletableFuture for asynchronous operations

@Service // Marks this class as a Spring service
public class BookService { // Service class for Book operations

    @Autowired // Injects the BookRepository bean
    private BookRepository bookRepository; // Repository for book data access

    @Autowired // Injects the BookCopyRepository bean
    private BookCopyRepository bookCopyRepository; // Repository for book copy data access

    @Autowired // Injects the LibraryRepository bean
    private LibraryRepository libraryRepository; // Repository for library data access

    public Page<Book> findAll(String author, Pageable pageable) { // Method to find all books, optionally filtered by author, with pagination
        if (author != null && !author.isEmpty()) { // Check if author filter is provided
            return bookRepository.findByAuthorContaining(author, pageable); // Return filtered books
        }
        return bookRepository.findAll(pageable); // Return all books if no filter is provided
    }

    public Optional<Book> findById(Long id) { // Method to find a book by its ID
        return bookRepository.findById(id); // Returns an Optional containing the book if found
    }

    public Book save(Book book) { // Method to save or update a book
        return bookRepository.save(book); // Saves the book to the database
    }

    @Transactional // Ensures this method runs within a database transaction
    public void moveBookCopy(Long bookCopyId, Long targetLibraryId) { // Method to move a book copy to another library
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(() -> new RuntimeException("BookCopy not found")); // Find book copy or throw exception
        Library targetLibrary = libraryRepository.findById(targetLibraryId).orElseThrow(() -> new RuntimeException("Target Library not found")); // Find target library or throw exception

        bookCopy.setLibrary(targetLibrary); // Update the library of the book copy
        bookCopyRepository.save(bookCopy); // Save the updated book copy
    }

    // Long Running Operation (LRO) simulation
    public CompletableFuture<String> searchFullText(String query) { // Method to simulate a long-running full-text search
        return CompletableFuture.supplyAsync(() -> { // Run the task asynchronously
            try { // Start try block
                // Simulate long processing time
                Thread.sleep(5000); // Sleep for 5 seconds
                // In a real scenario, we would search through all books' text
                // For now, returning a dummy result
                return "Search completed for query: " + query + ". Found 0 matches."; // Return search result
            } catch (InterruptedException e) { // Catch InterruptedException
                Thread.currentThread().interrupt(); // Restore interrupted status
                return "Search interrupted"; // Return interruption message
            }
        });
    }
}
