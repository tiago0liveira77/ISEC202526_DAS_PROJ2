package com.isec.das.project2.controller;

import com.isec.das.project2.model.Library;
import com.isec.das.project2.service.LibraryService;
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
@RequestMapping("/libraries")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLibraries(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String pageToken,
            @RequestParam(defaultValue = "10") int maxResults,
            @RequestParam(required = false) String fields) {

        int page = com.isec.das.project2.util.PaginationUtil.getPageFromToken(pageToken);
        Pageable pageable = PageRequest.of(page, maxResults);
        Page<Library> libraries = libraryService.findAll(location, pageable);

        List<Map<String, Object>> content = libraries.getContent().stream()
                .map(library -> FieldMaskUtil.applyFieldMask(library, fields))
                .collect(Collectors.toList());

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("data", content);

        String nextPageToken = com.isec.das.project2.util.PaginationUtil.getNextPageToken(page,
                libraries.getNumberOfElements(), maxResults);
        if (nextPageToken != null) {
            response.put("nextPageToken", nextPageToken);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLibraryById(
            @PathVariable Long id,
            @RequestParam(required = false) String fields) {
        return libraryService.findById(id)
                .map(library -> ResponseEntity.ok(FieldMaskUtil.applyFieldMask(library, fields)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createLibrary(@RequestBody Library library,
            @RequestParam(required = false) String fields) {
        Library saved = libraryService.save(library);
        return ResponseEntity.ok(FieldMaskUtil.applyFieldMask(saved, fields));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> replaceLibrary(@PathVariable Long id, @RequestBody Library library,
            @RequestParam(required = false) String fields) {
        return libraryService.findById(id)
                .map(existing -> {
                    library.setId(id);
                    Library saved = libraryService.save(library);
                    return ResponseEntity.ok(FieldMaskUtil.applyFieldMask(saved, fields));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateLibrary(@PathVariable Long id,
            @RequestBody Map<String, Object> updates, @RequestParam(required = false) String fields) {
        return libraryService.findById(id)
                .map(existing -> {
                    updates.forEach((k, v) -> {
                        try {
                            java.lang.reflect.Field field = org.springframework.util.ReflectionUtils
                                    .findField(Library.class, k);
                            if (field != null) {
                                field.setAccessible(true);
                                field.set(existing, v);
                            }
                        } catch (Exception e) {
                            // ignore
                        }
                    });
                    Library saved = libraryService.save(existing);
                    return ResponseEntity.ok(FieldMaskUtil.applyFieldMask(saved, fields));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibrary(@PathVariable Long id) {
        if (libraryService.findById(id).isPresent()) {
            libraryService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/registrations")
    public ResponseEntity<Void> registerUser(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        if (!body.containsKey("personId")) {
            return ResponseEntity.badRequest().build();
        }
        libraryService.registerUser(id, body.get("personId"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/registrations/{personId}")
    public ResponseEntity<Void> unregisterUser(@PathVariable Long id, @PathVariable Long personId) {
        libraryService.unregisterUser(id, personId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<Map<String, Object>>> getLibraryBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fields) {

        Pageable pageable = PageRequest.of(page, size);
        Page<com.isec.das.project2.model.BookCopy> bookCopies = libraryService.getLibraryBooks(id, pageable);

        List<Map<String, Object>> response = bookCopies.getContent().stream()
                .map(copy -> FieldMaskUtil.applyFieldMask(copy, fields))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
