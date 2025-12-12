package com.isec.das.project2.service;

import com.isec.das.project2.model.Book;
import com.isec.das.project2.model.BookCopy;
import com.isec.das.project2.model.Library;
import com.isec.das.project2.model.Operation;
import com.isec.das.project2.repository.BookCopyRepository;
import com.isec.das.project2.repository.BookRepository;
import com.isec.das.project2.repository.LibraryRepository;
import com.isec.das.project2.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private OperationRepository operationRepository;

    public Page<Book> findAll(String author, Pageable pageable) {
        if (author != null && !author.isEmpty()) {
            return bookRepository.findByAuthorContaining(author, pageable);
        }
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Transactional
    public void moveBookCopy(Long bookCopyId, Long targetLibraryId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new RuntimeException("BookCopy not found"));
        Library targetLibrary = libraryRepository.findById(targetLibraryId)
                .orElseThrow(() -> new RuntimeException("Target Library not found"));

        bookCopy.setLibrary(targetLibrary);
        bookCopyRepository.save(bookCopy);
    }

    @Transactional
    public void copyBookCopy(Long bookCopyId, Long targetLibraryId) {
        BookCopy original = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new RuntimeException("Original BookCopy not found"));
        Library targetLibrary = libraryRepository.findById(targetLibraryId)
                .orElseThrow(() -> new RuntimeException("Target Library not found"));

        BookCopy copy = new BookCopy();
        copy.setBook(original.getBook());
        copy.setLibrary(targetLibrary);
        // ID is auto-generated, satisfying "Generate new identifiers"
        bookCopyRepository.save(copy);
    }

    public Operation initiateSearch(String query) {
        Operation operation = new Operation();
        operation.setStatus("RUNNING");
        operation = operationRepository.save(operation);

        processSearchAsync(operation.getId(), query);

        return operation;
    }

    @Async
    public void processSearchAsync(String opId, String query) {
        try {
            Thread.sleep(10000); // 10 seconds to demonstrate LRO
            // Dummy search logic
            long count = bookRepository.count();

            Operation op = operationRepository.findById(opId).orElse(null);
            if (op != null) {
                op.setStatus("DONE");
                op.setResult("{\"matches\": " + count + "}");
                operationRepository.save(op);
            }
        } catch (Exception e) {
            Operation op = operationRepository.findById(opId).orElse(null);
            if (op != null) {
                op.setStatus("FAILED");
                op.setResult("{\"error\": \"" + e.getMessage() + "\"}");
                operationRepository.save(op);
            }
        }
    }
}
