package com.isec.das.project2.repository;

import com.isec.das.project2.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByLibraryIdAndPersonId(Long libraryId, Long personId);
}
