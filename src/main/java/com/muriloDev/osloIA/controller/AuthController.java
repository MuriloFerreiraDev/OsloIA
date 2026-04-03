package com.muriloDev.osloIA.controller;

import com.muriloDev.osloIA.dto.request.LoginRequest;
import com.muriloDev.osloIA.dto.request.RegisterRequest;
import com.muriloDev.osloIA.dto.response.LoginResponse;
import com.muriloDev.osloIA.dto.response.MessageResponse;
import com.muriloDev.osloIA.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}