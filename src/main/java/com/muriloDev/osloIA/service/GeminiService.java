package com.muriloDev.osloIA.service;

import com.muriloDev.osloIA.domain.model.ChatHistory;
import com.muriloDev.osloIA.domain.model.User;
import com.muriloDev.osloIA.dto.request.ChatRequest;
import com.muriloDev.osloIA.dto.response.ChatHistoryResponse;
import com.muriloDev.osloIA.dto.response.ChatResponse;
import com.muriloDev.osloIA.repository.ChatHistoryRepository;
import com.muriloDev.osloIA.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ChatHistoryRepository chatHistoryRepository;
    private final UserRepository userRepository;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    public ChatResponse chat(ChatRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String aiResponse = callGemini(request.getMessage());

        ChatHistory history = ChatHistory.builder()
                .user(user)
                .userMessage(request.getMessage())
                .aiResponse(aiResponse)
                .build();

        chatHistoryRepository.save(history);
        return new ChatResponse(request.getMessage(), aiResponse);
    }

    public List<ChatHistoryResponse> getHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return chatHistoryRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(h -> new ChatHistoryResponse(
                        h.getId(),
                        h.getUserMessage(),
                        h.getAiResponse(),
                        h.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    private String callGemini(String message) {
        Map<String, String> part = new HashMap<>();
        part.put("text", message);

        List<Map<String, String>> parts = new ArrayList<>();
        parts.add(part);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", parts);

        List<Map<String, Object>> contents = new ArrayList<>();
        contents.add(content);

        Map<String, Object> body = new HashMap<>();
        body.put("contents", contents);

        Map response = webClient.post()
                .uri("/v1beta/models/gemini-2.5-flash:generateContent")
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List candidates = (List) response.get("candidates");
        Map candidate = (Map) candidates.get(0);
        Map content2 = (Map) candidate.get("content");
        List responseParts = (List) content2.get("parts");
        Map part2 = (Map) responseParts.get(0);
        return (String) part2.get("text");
    }
}