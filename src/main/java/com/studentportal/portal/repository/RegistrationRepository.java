package com.studentportal.portal.repository;

import com.studentportal.portal.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // Find all registrations made by a specific student
    List<Registration> findByStudentIdOrderByRegistrationDateDesc(Long studentId);
}
