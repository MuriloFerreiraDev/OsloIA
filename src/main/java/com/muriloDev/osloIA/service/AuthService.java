package com.muriloDev.osloIA.service;

import com.muriloDev.osloIA.domain.enums.Role;
import com.muriloDev.osloIA.domain.model.User;
import com.muriloDev.osloIA.dto.request.LoginRequest;
import com.muriloDev.osloIA.dto.request.RegisterRequest;
import com.muriloDev.osloIA.dto.response.LoginResponse;
import com.muriloDev.osloIA.dto.response.MessageResponse;
import com.muriloDev.osloIA.repository.UserRepository;
import com.muriloDev.osloIA.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return new MessageResponse("User registered successfully!");
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getName(), user.getEmail());
    }
}