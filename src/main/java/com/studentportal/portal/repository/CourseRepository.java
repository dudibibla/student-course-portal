package com.studentportal.portal.repository;

import com.studentportal.portal.entity.Course;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Fetch all courses with their teacher in a single query (avoids N+1 in the catalog)
    @Query("select c from Course c left join fetch c.teacher order by c.name")
    List<Course> findAllWithTeacher();

    // Fetch the cart's courses with their teacher already initialized, so the
    // cart view never touches a lazy proxy (safe even after a rolled-back checkout)
    @Query("select c from Course c left join fetch c.teacher where c.id in :ids")
    List<Course> findAllByIdWithTeacher(@Param("ids") List<Long> ids);

    // Lock the course row during checkout so two concurrent registrations
    // cannot both pass the capacity check (prevents TOCTOU race condition)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Course c where c.id = :id")
    Optional<Course> findByIdForUpdate(@Param("id") Long id);
}
