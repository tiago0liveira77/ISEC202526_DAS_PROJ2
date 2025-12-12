package com.isec.das.project2; // Package declaration for test classes

import com.isec.das.project2.service.BookService; // Import BookService
import org.junit.jupiter.api.Test; // Import JUnit Test annotation
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired
import org.springframework.boot.test.context.SpringBootTest; // Import SpringBootTest

import java.util.concurrent.CompletableFuture; // Import CompletableFuture
import java.util.concurrent.ExecutionException; // Import ExecutionException

import static org.junit.jupiter.api.Assertions.*; // Import assertions

@SpringBootTest // Loads the full application context for integration testing
public class LROIntegrationTest { // Test class for Long Running Operations

    @Autowired // Injects the BookService bean
    private BookService bookService; // Service to be tested

    @Test // Marks this method as a test case
    public void testSearchFullTextLRO() throws ExecutionException, InterruptedException { // Test method for LRO search
        // Given
        String query = "Spring"; // Define the search query
        long startTime = System.currentTimeMillis(); // Record start time

        // When
        System.out.println("Starting LRO Search..."); // Log start
        CompletableFuture<String> futureResult = bookService.searchFullText(query); // Call the async method

        // Then
        assertNotNull(futureResult, "The returned CompletableFuture should not be null"); // Verify future is not null
        assertFalse(futureResult.isDone(), "The task should not be done immediately (simulating LRO)"); // Verify it's
                                                                                                        // running
                                                                                                        // asynchronously

        // Wait for the result (blocking this test thread)
        String result = futureResult.get(); // Get the result, waiting if necessary
        long duration = System.currentTimeMillis() - startTime; // Calculate duration

        System.out.println("LRO Search finished in " + duration + "ms"); // Log duration
        System.out.println("Result: " + result); // Log result

        // Assertions
        assertTrue(duration >= 5000, "The operation should take at least 5000ms as simulated"); // Verify duration
                                                                                                // matches simulation
        assertNotNull(result, "The result should not be null"); // Verify result is not null
        assertTrue(result.contains(query), "The result should contain the query string"); // Verify result content
    }
}
