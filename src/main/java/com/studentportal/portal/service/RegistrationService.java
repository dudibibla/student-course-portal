package com.studentportal.portal.service;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.Registration;
import com.studentportal.portal.entity.RegistrationItem;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.repository.CourseRepository;
import com.studentportal.portal.repository.RegistrationRepository;
import com.studentportal.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalDouble;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Registration checkoutCart(Long studentId, List<Long> courseIds) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (courseIds == null || courseIds.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Registration registration = new Registration(student);

        for (Long courseId : courseIds) {
            // Pessimistic lock: the course row stays locked until the transaction
            // commits, so concurrent checkouts cannot both pass the capacity check
            Course course = courseRepository.findByIdForUpdate(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

            // Verify capacity
            int currentRegistrations = course.getRegistrationItems().size();
            if (currentRegistrations >= course.getMaxStudents()) {
                throw new IllegalStateException("Course is full: " + course.getName());
            }

            // Verify student isn't already registered for this course
            boolean alreadyRegistered = student.getRegistrations().stream()
                    .flatMap(reg -> reg.getItems().stream())
                    .anyMatch(item -> item.getCourse().getId().equals(courseId));

            if (alreadyRegistered) {
                throw new IllegalStateException("Student is already registered for course: " + course.getName());
            }

            RegistrationItem item = new RegistrationItem();
            item.setCourse(course);
            registration.addItem(item);
        }

        return registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAllWithStudentAndItems();
    }

    @Transactional(readOnly = true)
    public List<Registration> getRegistrationsForStudent(Long studentId) {
        return registrationRepository.findByStudentIdWithItems(studentId);
    }

    /**
     * Average of all grades the student has received across registrations,
     * or null when no grades have been assigned yet.
     */
    @Transactional(readOnly = true)
    public Double calculateAverageGrade(Long studentId) {
        OptionalDouble average = registrationRepository.findByStudentIdWithItems(studentId).stream()
                .flatMap(reg -> reg.getItems().stream())
                .map(RegistrationItem::getGrade)
                .filter(grade -> grade != null)
                .mapToInt(Integer::intValue)
                .average();
        return average.isPresent() ? average.getAsDouble() : null;
    }
}
