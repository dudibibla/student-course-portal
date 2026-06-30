package com.studentportal.portal.repository;

import com.studentportal.portal.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    List<CourseReview> findByCourseIdOrderByTimestampDesc(Long courseId);
}
