package com.isec.das.project2.controller;

import com.isec.das.project2.model.Operation;
import com.isec.das.project2.repository.OperationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operations")
public class OperationController {

    private final OperationRepository operationRepository;

    public OperationController(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Operation> obter(@PathVariable Long id) {

        Operation operation = operationRepository.findById(id).orElse(null);

        if (operation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(operation);

    }
}
