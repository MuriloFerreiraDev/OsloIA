package com.muriloDev.osloIA.controller;

import com.muriloDev.osloIA.dto.request.ChatRequest;
import com.muriloDev.osloIA.dto.response.ChatHistoryResponse;
import com.muriloDev.osloIA.dto.response.ChatResponse;
import com.muriloDev.osloIA.service.GeminiService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final GeminiService geminiService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(geminiService.chat(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatHistoryResponse>> getHistory() {
        return ResponseEntity.ok(geminiService.getHistory());
    }
}