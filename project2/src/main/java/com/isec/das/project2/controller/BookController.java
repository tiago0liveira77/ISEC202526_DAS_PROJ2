package com.isec.das.project2.controller;

import com.isec.das.project2.model.Book;
import com.isec.das.project2.model.Operation;
import com.isec.das.project2.service.BookService;
import com.isec.das.project2.util.FieldMaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String pageToken,
            @RequestParam(defaultValue = "10") int maxResults,
            @RequestParam(required = false) String fields) {

        int page = com.isec.das.project2.util.PaginationUtil.getPageFromToken(pageToken);
        Pageable pageable = PageRequest.of(page, maxResults);
        Page<Book> books = bookService.findAll(author, pageable);

        List<Map<String, Object>> content = books.getContent().stream()
                .map(book -> FieldMaskUtil.applyFieldMask(book, fields))
                .collect(Collectors.toList());

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("data", content);

        String nextPageToken = com.isec.das.project2.util.PaginationUtil.getNextPageToken(page,
                books.getNumberOfElements(), maxResults);
        if (nextPageToken != null) {
            response.put("nextPageToken", nextPageToken);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookById(
            @PathVariable Long id,
            @RequestParam(required = false) String fields) {
        return bookService.findById(id)
                .map(book -> ResponseEntity.ok(FieldMaskUtil.applyFieldMask(book, fields)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBook(@RequestBody Book book,
            @RequestParam(required = false) String fields) {
        Book saved = bookService.save(book);
        return ResponseEntity.ok(FieldMaskUtil.applyFieldMask(saved, fields));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> replaceBook(@PathVariable Long id, @RequestBody Book book,
            @RequestParam(required = false) String fields) {
        return bookService.findById(id)
                .map(existing -> {
                    book.setId(id);
                    Book saved = bookService.save(book);
                    return ResponseEntity.ok(FieldMaskUtil.applyFieldMask(saved, fields));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBook(@PathVariable Long id,
            @RequestBody Map<String, Object> updates, @RequestParam(required = false) String fields) {
        return bookService.findById(id)
                .map(existing -> {
                    updates.forEach((k, v) -> {
                        try {
                            java.lang.reflect.Field field = org.springframework.util.ReflectionUtils
                                    .findField(Book.class, k);
                            if (field != null) {
                                field.setAccessible(true);
                                field.set(existing, v);
                            }
                        } catch (Exception e) {
                            // ignore
                        }
                    });
                    Book saved = bookService.save(existing);
                    return ResponseEntity.ok(FieldMaskUtil.applyFieldMask(saved, fields));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (bookService.findById(id).isPresent()) {
            bookService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Custom Method: Move Copy
    @PostMapping("/copies/{id}:move")
    public ResponseEntity<Void> moveBookCopy(@PathVariable Long id, @RequestBody Map<String, Long> updates) {
        if (updates.containsKey("libraryId")) {
            bookService.moveBookCopy(id, updates.get("libraryId"));
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    // Custom Method: Copy Copy
    @PostMapping("/copies/{id}:copy")
    public ResponseEntity<Void> copyBookCopy(@PathVariable Long id, @RequestBody Map<String, Long> updates) {
        if (updates.containsKey("targetLibraryId")) {
            bookService.copyBookCopy(id, updates.get("targetLibraryId"));
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    // Singleton sub-resource for text
    @GetMapping("/{id}/text") // Maps GET requests for book text to this method
    public ResponseEntity<String> getBookText(@PathVariable Long id) { // Method to get book text
        return bookService.findById(id) // Find book by ID
                .map(book -> ResponseEntity.ok(book.getText() != null ? book.getText() : "")) // If found, return text
                                                                                              // or empty string
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    @PutMapping("/{id}/text")
    public ResponseEntity<Void> updateBookText(@PathVariable Long id, @RequestBody String text) {
        return bookService.findById(id)
                .map(book -> {
                    book.setText(text);
                    bookService.save(book);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // LRO Search - Custom Method
    @PostMapping(":search")
    public ResponseEntity<Operation> searchBooks(@RequestParam String query) {
        return ResponseEntity.ok(bookService.initiateSearch(query));
    }
}
