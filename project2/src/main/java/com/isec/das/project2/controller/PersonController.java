package com.isec.das.project2.controller;

import com.isec.das.project2.model.Loan;
import com.isec.das.project2.model.Person;
import com.isec.das.project2.service.PersonService;
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
@RequestMapping("/people")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPeople(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String pageToken,
            @RequestParam(defaultValue = "10") int maxResults,
            @RequestParam(required = false) String fields) {

        int page = com.isec.das.project2.util.PaginationUtil.getPageFromToken(pageToken);
        Pageable pageable = PageRequest.of(page, maxResults);
        Page<Person> people = personService.findAll(name, pageable);

        List<Map<String, Object>> content = people.getContent().stream()
                .map(person -> FieldMaskUtil.applyFieldMask(person, fields))
                .collect(Collectors.toList());

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("data", content);

        String nextPageToken = com.isec.das.project2.util.PaginationUtil.getNextPageToken(page,
                people.getNumberOfElements(), maxResults);
        if (nextPageToken != null) {
            response.put("nextPageToken", nextPageToken);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPersonById(
            @PathVariable Long id,
            @RequestParam(required = false) String fields) {
        return personService.findById(id)
                .map(person -> ResponseEntity.ok(FieldMaskUtil.applyFieldMask(person, fields)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPerson(@RequestBody Person person,
            @RequestParam(required = false) String fields) {
        Person saved = personService.save(person);
        return ResponseEntity.ok(FieldMaskUtil.applyFieldMask(saved, fields));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> replacePerson(@PathVariable Long id, @RequestBody Person person,
            @RequestParam(required = false) String fields) {
        return personService.findById(id)
                .map(existing -> {
                    person.setId(id);
                    Person saved = personService.save(person);
                    return ResponseEntity.ok(FieldMaskUtil.applyFieldMask(saved, fields));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePerson(@PathVariable Long id,
            @RequestBody Map<String, Object> updates, @RequestParam(required = false) String fields) {
        return personService.findById(id)
                .map(existing -> {
                    updates.forEach((k, v) -> {
                        try {
                            java.lang.reflect.Field field = org.springframework.util.ReflectionUtils
                                    .findField(Person.class, k);
                            if (field != null) {
                                field.setAccessible(true);
                                field.set(existing, v);
                            }
                        } catch (Exception e) {
                            // ignore
                        }
                    });
                    Person saved = personService.save(existing);
                    return ResponseEntity.ok(FieldMaskUtil.applyFieldMask(saved, fields));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        if (personService.findById(id).isPresent()) {
            personService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/loans")
    public ResponseEntity<Loan> loanBook(@PathVariable Long id, @RequestParam Long bookCopyId) {
        return ResponseEntity.ok(personService.loanBook(id, bookCopyId));
    }

    // Custom Method: Return Book
    @PostMapping("/{id}/loans/{loanId}:return")
    public ResponseEntity<Void> returnBook(@PathVariable Long id, @PathVariable Long loanId) {
        personService.returnBook(loanId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/loans")
    public ResponseEntity<List<Loan>> getLoans(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean active) {

        if (active != null) {
            if (active) {
                return ResponseEntity.ok(personService.getCurrentLoans(id));
            } else {
                return ResponseEntity.ok(personService.getLoanHistory(id));
            }
        }
        return ResponseEntity.ok(personService.getCurrentLoans(id));
    }
}
