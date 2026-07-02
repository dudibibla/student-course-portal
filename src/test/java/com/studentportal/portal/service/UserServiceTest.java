package com.studentportal.portal.service;

import com.studentportal.portal.entity.Role;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerUser_encodesPasswordBeforeSaving() {
        User user = new User("Test", "new@test.com", "plaintext", Role.STUDENT);
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.registerUser(user);

        assertThat(saved.getPassword()).isNotEqualTo("plaintext");
        assertThat(passwordEncoder.matches("plaintext", saved.getPassword())).isTrue();
    }

    @Test
    void registerUser_rejectsDuplicateEmail() {
        User existing = new User("Existing", "taken@test.com", "password", Role.STUDENT);
        when(userRepository.findByEmail("taken@test.com")).thenReturn(Optional.of(existing));

        User user = new User("Test", "taken@test.com", "password", Role.STUDENT);

        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already in use");

        verify(userRepository, never()).save(any());
    }
}
