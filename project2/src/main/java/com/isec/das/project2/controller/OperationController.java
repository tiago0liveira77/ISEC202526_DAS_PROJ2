package com.isec.das.project2.controller;

import com.isec.das.project2.model.Operation;
import com.isec.das.project2.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operations")
public class OperationController {

    @Autowired
    private OperationRepository operationRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Operation> getOperation(@PathVariable String id) {
        return operationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
