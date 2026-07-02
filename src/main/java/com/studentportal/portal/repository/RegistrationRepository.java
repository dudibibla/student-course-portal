package com.studentportal.portal.repository;

import com.studentportal.portal.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // Fetch registrations for the teacher dashboard with student and items
    // in a single query (avoids N+1 lazy loading per row)
    @Query("select distinct r from Registration r join fetch r.student left join fetch r.items order by r.registrationDate desc")
    List<Registration> findAllWithStudentAndItems();

    // Fetch a student's registrations with items and courses for the student dashboard
    @Query("select distinct r from Registration r left join fetch r.items i left join fetch i.course where r.student.id = :studentId order by r.registrationDate desc")
    List<Registration> findByStudentIdWithItems(@Param("studentId") Long studentId);
}
