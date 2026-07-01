package com.studentportal.portal.service;

import com.studentportal.portal.entity.Assignment;
import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.entity.RegistrationItem;
import com.studentportal.portal.repository.AssignmentRepository;
import com.studentportal.portal.repository.CourseRepository;
import com.studentportal.portal.repository.RegistrationItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final RegistrationItemRepository registrationItemRepository;
    private final AssignmentRepository assignmentRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, RegistrationItemRepository registrationItemRepository, AssignmentRepository assignmentRepository) {
        this.courseRepository = courseRepository;
        this.registrationItemRepository = registrationItemRepository;
        this.assignmentRepository = assignmentRepository;
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

    @Transactional
    public Course assignTeacher(Long courseId, User teacher) {
        Optional<Course> opt = courseRepository.findById(courseId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Course not found");
        }
        Course course = opt.get();
        // Override existing teacher (or could add logic for admin only)
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    @Transactional
    public void updateRegistrationItemGrade(Long itemId, Integer grade, User user) {
        RegistrationItem item = registrationItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Registration item not found"));
        
        Course course = item.getCourse();
        if (user.getRole() != com.studentportal.portal.entity.Role.ADMIN && 
            (course.getTeacher() == null || !course.getTeacher().getId().equals(user.getId()))) {
            throw new IllegalStateException("Not authorized to update grades for this course");
        }
        
        item.setGrade(grade);
        registrationItemRepository.save(item);
    }

    @Transactional
    public void addAssignment(Long courseId, String title, String description, java.time.LocalDate dueDate, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        if (user.getRole() != com.studentportal.portal.entity.Role.ADMIN && 
            (course.getTeacher() == null || !course.getTeacher().getId().equals(user.getId()))) {
            throw new IllegalStateException("Not authorized to add assignments to this course");
        }
        
        Assignment assignment = new Assignment(title, description, dueDate, course);
        assignmentRepository.save(assignment);
    }
}
