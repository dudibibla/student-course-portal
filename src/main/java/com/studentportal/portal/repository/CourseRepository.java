package com.studentportal.portal.repository;

import com.studentportal.portal.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find all courses taught by a specific teacher
    List<Course> findByTeacherId(Long teacherId);
    
    // Search courses by name containing a keyword (case insensitive)
    List<Course> findByNameContainingIgnoreCase(String keyword);
}
