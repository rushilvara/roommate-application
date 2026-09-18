package com.roommate.service;

import com.roommate.dto.RegisterRequest;
import com.roommate.model.User;
import com.roommate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerHashesPasswordAndStoresNormalizedEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alex");
        request.setEmail("Alex@Example.Com ");
        request.setPassword("secret123");

        when(userRepository.existsByEmailIgnoreCase("Alex@Example.Com ")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User persisted = captor.getValue();

        assertThat(saved.getEmail()).isEqualTo("alex@example.com");
        assertThat(persisted.getPasswordHash()).isNotEqualTo("secret123");
        assertThat(passwordEncoder.matches("secret123", persisted.getPasswordHash())).isTrue();
    }

    @Test
    void registerThrowsOnDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alex");
        request.setEmail("alex@example.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmailIgnoreCase("alex@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void authenticateReturnsUserWhenPasswordMatches() {
        User user = new User();
        user.setEmail("alex@example.com");
        user.setPasswordHash(passwordEncoder.encode("secret123"));

        when(userRepository.findByEmailIgnoreCase("alex@example.com")).thenReturn(Optional.of(user));

        Optional<User> authenticated = userService.authenticate("alex@example.com", "secret123");

        assertThat(authenticated).isPresent();
    }
}
