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
        // Pegar usuário logado
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Chamar o Gemini
        String aiResponse = callGemini(request.getMessage());

        // Salvar no histórico
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
        String url = "/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", message)
                        ))
                )
        );

        Map response = webClient.post()
                .uri(url)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List candidates = (List) response.get("candidates");
        Map candidate = (Map) candidates.get(0);
        Map content = (Map) candidate.get("content");
        List parts = (List) content.get("parts");
        Map part = (Map) parts.get(0);

        return (String) part.get("text");
    }
}