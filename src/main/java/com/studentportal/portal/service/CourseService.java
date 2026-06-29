package com.studentportal.portal.service;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    public Course createCourse(Course course, User teacher) {
        // Assume teacher is already validated as having Role.TEACHER or ADMIN
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public int getAvailableSpots(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return 0;
        }
        Course course = courseOpt.get();
        int currentRegistrations = course.getRegistrationItems().size();
        return Math.max(0, course.getMaxStudents() - currentRegistrations);
    }
}
