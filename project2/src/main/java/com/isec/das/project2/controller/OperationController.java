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
        return operationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
