package com.studentportal.portal.controller;

import com.studentportal.portal.dto.RegisterForm;
import com.studentportal.portal.entity.Role;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(userService);
    }

    private RegisterForm validForm() {
        RegisterForm form = new RegisterForm();
        form.setName("New Student");
        form.setEmail("new@student.com");
        form.setPassword("secret123");
        return form;
    }

    @Test
    void selfRegistration_alwaysCreatesStudentRole() {
        // Regression test for the privilege-escalation fix: no matter what is
        // posted, a self-registered account must be created as STUDENT
        RegisterForm form = validForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "user");

        String view = authController.registerUser(form, bindingResult, new ExtendedModelMap());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).registerUser(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(Role.STUDENT);
        assertThat(view).isEqualTo("redirect:/login?registered=true");
    }

    @Test
    void registration_returnsFormWhenValidationFails() {
        RegisterForm form = validForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "user");
        bindingResult.rejectValue("email", "invalid", "Email must be a valid address");

        String view = authController.registerUser(form, bindingResult, new ExtendedModelMap());

        assertThat(view).isEqualTo("register");
        verify(userService, never()).registerUser(any());
    }

    @Test
    void registration_showsErrorWhenEmailAlreadyExists() {
        RegisterForm form = validForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "user");
        when(userService.registerUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Email is already in use"));

        ExtendedModelMap model = new ExtendedModelMap();
        String view = authController.registerUser(form, bindingResult, model);

        assertThat(view).isEqualTo("register");
        assertThat(model.getAttribute("error")).isEqualTo("Email is already in use");
    }
}
