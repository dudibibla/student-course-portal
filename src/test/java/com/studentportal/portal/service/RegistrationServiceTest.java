package com.studentportal.portal.service;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.Registration;
import com.studentportal.portal.entity.RegistrationItem;
import com.studentportal.portal.entity.Role;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.repository.CourseRepository;
import com.studentportal.portal.repository.RegistrationRepository;
import com.studentportal.portal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    private RegistrationService registrationService;

    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationService(registrationRepository, courseRepository, userRepository);

        student = new User("Test Student", "student@test.com", "password", Role.STUDENT);
        student.setId(1L);

        course = new Course("Test Course", "A course", 2, null);
        course.setId(10L);
    }

    @Test
    void checkoutCart_savesRegistrationWithAllCourses() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(course));
        when(registrationRepository.save(any(Registration.class))).thenAnswer(inv -> inv.getArgument(0));

        Registration result = registrationService.checkoutCart(1L, List.of(10L));

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getCourse()).isEqualTo(course);
    }

    @Test
    void checkoutCart_throwsWhenCartIsEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> registrationService.checkoutCart(1L, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cart is empty");

        verify(registrationRepository, never()).save(any());
    }

    @Test
    void checkoutCart_throwsWhenStudentNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.checkoutCart(99L, List.of(10L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void checkoutCart_throwsWhenCourseIsFull() {
        // Fill the course to its capacity of 2
        course.getRegistrationItems().add(new RegistrationItem());
        course.getRegistrationItems().add(new RegistrationItem());

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> registrationService.checkoutCart(1L, List.of(10L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Course is full");

        verify(registrationRepository, never()).save(any());
    }

    @Test
    void checkoutCart_throwsWhenStudentAlreadyRegistered() {
        Registration existing = new Registration(student);
        RegistrationItem existingItem = new RegistrationItem();
        existingItem.setCourse(course);
        existing.addItem(existingItem);
        student.getRegistrations().add(existing);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> registrationService.checkoutCart(1L, List.of(10L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already registered");

        verify(registrationRepository, never()).save(any());
    }

    @Test
    void calculateAverageGrade_returnsAverageOfGradedItemsOnly() {
        Registration reg = new Registration(student);
        RegistrationItem graded1 = new RegistrationItem();
        graded1.setGrade(80);
        RegistrationItem graded2 = new RegistrationItem();
        graded2.setGrade(90);
        RegistrationItem ungraded = new RegistrationItem();
        reg.addItem(graded1);
        reg.addItem(graded2);
        reg.addItem(ungraded);

        when(registrationRepository.findByStudentIdWithItems(1L)).thenReturn(List.of(reg));

        assertThat(registrationService.calculateAverageGrade(1L)).isEqualTo(85.0);
    }

    @Test
    void calculateAverageGrade_returnsNullWhenNoGrades() {
        when(registrationRepository.findByStudentIdWithItems(1L)).thenReturn(List.of());

        assertThat(registrationService.calculateAverageGrade(1L)).isNull();
    }
}
