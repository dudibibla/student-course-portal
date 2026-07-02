package com.studentportal.portal.service;

import com.studentportal.portal.entity.Assignment;
import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.RegistrationItem;
import com.studentportal.portal.entity.Role;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.repository.AssignmentRepository;
import com.studentportal.portal.repository.CourseRepository;
import com.studentportal.portal.repository.RegistrationItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        return courseRepository.findAllWithTeacher();
    }

    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    public Course createCourse(Course course, User teacher) {
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Course name is required");
        }
        if (course.getMaxStudents() < 1) {
            throw new IllegalArgumentException("Max students must be at least 1");
        }
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
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    @Transactional
    public void updateRegistrationItemGrade(Long itemId, Integer grade, User user) {
        if (grade == null || grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }

        RegistrationItem item = registrationItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Registration item not found"));

        Course course = item.getCourse();
        if (!canManageCourse(user, course)) {
            throw new IllegalStateException("Not authorized to update grades for this course");
        }

        item.setGrade(grade);
        registrationItemRepository.save(item);
    }

    @Transactional
    public void addAssignment(Long courseId, String title, String description, LocalDate dueDate, User user) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Assignment title is required");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Assignment due date is required");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!canManageCourse(user, course)) {
            throw new IllegalStateException("Not authorized to add assignments to this course");
        }

        Assignment assignment = new Assignment(title, description, dueDate, course);
        assignmentRepository.save(assignment);
    }

    // Admins manage every course; teachers only the courses they teach
    private boolean canManageCourse(User user, Course course) {
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        return course.getTeacher() != null && course.getTeacher().getId().equals(user.getId());
    }
}
