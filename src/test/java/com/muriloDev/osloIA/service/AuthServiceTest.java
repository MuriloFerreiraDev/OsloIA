package com.muriloDev.osloIA.service;

import com.muriloDev.osloIA.domain.enums.Role;
import com.muriloDev.osloIA.domain.model.User;
import com.muriloDev.osloIA.dto.request.LoginRequest;
import com.muriloDev.osloIA.dto.request.RegisterRequest;
import com.muriloDev.osloIA.dto.response.LoginResponse;
import com.muriloDev.osloIA.dto.response.MessageResponse;
import com.muriloDev.osloIA.repository.UserRepository;
import com.muriloDev.osloIA.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Murilo");
        registerRequest.setEmail("murilo@email.com");
        registerRequest.setPassword("123456");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("murilo@email.com");
        loginRequest.setPassword("123456");

        user = User.builder()
                .id(UUID.randomUUID())
                .name("Murilo")
                .email("murilo@email.com")
                .password("encoded_password")
                .role(Role.USER)
                .build();
    }

    // ✅ Teste 1 - Register com sucesso
    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        MessageResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("User registered successfully!");
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ✅ Teste 2 - Register com email duplicado
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    // ✅ Teste 3 - Login com sucesso
    @Test
    void shouldLoginSuccessfully() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("fake-jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getEmail()).isEqualTo("murilo@email.com");
        assertThat(response.getName()).isEqualTo("Murilo");
    }

    // ✅ Teste 4 - Login com senha errada
    @Test
    void shouldThrowExceptionWhenCredentialsAreInvalid() {
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ✅ Teste 5 - Login com usuário não encontrado
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }
}