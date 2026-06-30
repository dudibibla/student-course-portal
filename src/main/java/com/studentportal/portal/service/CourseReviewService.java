package com.studentportal.portal.service;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.CourseReview;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.repository.CourseRepository;
import com.studentportal.portal.repository.CourseReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseReviewService {

    private final CourseReviewRepository reviewRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseReviewService(CourseReviewRepository reviewRepository, CourseRepository courseRepository) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseReview> getReviewsForCourse(Long courseId) {
        return reviewRepository.findByCourseIdOrderByTimestampDesc(courseId);
    }

    @Transactional
    public CourseReview addReview(Long courseId, User student, String content) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid course ID"));
        
        CourseReview review = new CourseReview(course, student, content);
        return reviewRepository.save(review);
    }
}
