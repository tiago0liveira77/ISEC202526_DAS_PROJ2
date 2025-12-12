package com.isec.das.project2;

import com.isec.das.project2.model.Operation;
import com.isec.das.project2.repository.OperationRepository;
import com.isec.das.project2.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class LROIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private OperationRepository operationRepository;

    @Test
    public void testSearchFullTextLRO() {
        // Given
        String query = "Spring";

        // When
        System.out.println("Starting LRO Search...");
        Operation operation = bookService.initiateSearch(query);

        // Then
        assertNotNull(operation, "The returned Operation should not be null");
        assertNotNull(operation.getId(), "Operation ID should not be null");
        assertEquals("RUNNING", operation.getStatus(), "Initial status should be RUNNING");

        // Wait for completion (polling)
        await().atMost(Duration.ofSeconds(15)).until(() -> {
            Operation updated = operationRepository.findById(operation.getId()).orElse(null);
            return updated != null && "DONE".equals(updated.getStatus());
        });

        Operation finalOp = operationRepository.findById(operation.getId()).orElseThrow();
        assertEquals("DONE", finalOp.getStatus());
        assertNotNull(finalOp.getResult());
        assertTrue(finalOp.getResult().contains("matches"));
    }
}
