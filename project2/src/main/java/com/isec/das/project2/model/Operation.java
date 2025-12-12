package com.isec.das.project2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String status; // PENDING, RUNNING, DONE, FAILED

    @Lob
    private String result; // JSON result or error message

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
