package com.isec.das.project2.repository;

import com.isec.das.project2.model.FullText;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FullTextRepository extends JpaRepository<FullText, Long> {
}
