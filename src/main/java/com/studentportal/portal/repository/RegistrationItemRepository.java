package com.studentportal.portal.repository;

import com.studentportal.portal.entity.RegistrationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationItemRepository extends JpaRepository<RegistrationItem, Long> {

    // Find all registrations for a specific course
    List<RegistrationItem> findByCourseId(Long courseId);
}
