package com.muriloDev.osloIA.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ChatHistoryResponse {
    private UUID id;
    private String userMessage;
    private String aiResponse;
    private LocalDateTime createdAt;
}