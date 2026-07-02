package com.studentportal.portal.service;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.RegistrationItem;
import com.studentportal.portal.entity.Role;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.repository.AssignmentRepository;
import com.studentportal.portal.repository.CourseRepository;
import com.studentportal.portal.repository.RegistrationItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private RegistrationItemRepository registrationItemRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    private CourseService courseService;

    private User teacher;
    private User otherTeacher;
    private User admin;
    private Course course;
    private RegistrationItem item;

    @BeforeEach
    void setUp() {
        courseService = new CourseService(courseRepository, registrationItemRepository, assignmentRepository);

        teacher = new User("Teacher", "teacher@test.com", "password", Role.TEACHER);
        teacher.setId(1L);

        otherTeacher = new User("Other Teacher", "other@test.com", "password", Role.TEACHER);
        otherTeacher.setId(2L);

        admin = new User("Admin", "admin@test.com", "password", Role.ADMIN);
        admin.setId(3L);

        course = new Course("Course", "Description", 10, teacher);
        course.setId(10L);

        item = new RegistrationItem();
        item.setId(100L);
        item.setCourse(course);
    }

    @Test
    void updateGrade_succeedsForCourseTeacher() {
        when(registrationItemRepository.findById(100L)).thenReturn(Optional.of(item));

        courseService.updateRegistrationItemGrade(100L, 95, teacher);

        assertThat(item.getGrade()).isEqualTo(95);
        verify(registrationItemRepository).save(item);
    }

    @Test
    void updateGrade_succeedsForAdmin() {
        when(registrationItemRepository.findById(100L)).thenReturn(Optional.of(item));

        courseService.updateRegistrationItemGrade(100L, 88, admin);

        assertThat(item.getGrade()).isEqualTo(88);
    }

    @Test
    void updateGrade_rejectsTeacherOfAnotherCourse() {
        when(registrationItemRepository.findById(100L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> courseService.updateRegistrationItemGrade(100L, 95, otherTeacher))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not authorized");

        verify(registrationItemRepository, never()).save(any());
    }

    @Test
    void updateGrade_rejectsGradeAbove100() {
        assertThatThrownBy(() -> courseService.updateRegistrationItemGrade(100L, 101, teacher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 0 and 100");
    }

    @Test
    void updateGrade_rejectsNegativeGrade() {
        assertThatThrownBy(() -> courseService.updateRegistrationItemGrade(100L, -5, teacher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 0 and 100");
    }

    @Test
    void updateGrade_rejectsNullGrade() {
        assertThatThrownBy(() -> courseService.updateRegistrationItemGrade(100L, null, teacher))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createCourse_rejectsBlankName() {
        Course invalid = new Course("   ", "Description", 10, null);

        assertThatThrownBy(() -> courseService.createCourse(invalid, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void createCourse_rejectsNonPositiveCapacity() {
        Course invalid = new Course("Course", "Description", 0, null);

        assertThatThrownBy(() -> courseService.createCourse(invalid, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 1");
    }

    @Test
    void getAvailableSpots_neverReturnsNegative() {
        course.getRegistrationItems().add(new RegistrationItem());
        course.getRegistrationItems().add(new RegistrationItem());
        course.setMaxStudents(1);
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

        assertThat(courseService.getAvailableSpots(10L)).isZero();
    }

    @Test
    void addAssignment_rejectsTeacherOfAnotherCourse() {
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.addAssignment(10L, "HW1", "Desc", java.time.LocalDate.now(), otherTeacher))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not authorized");

        verify(assignmentRepository, never()).save(any());
    }
}
