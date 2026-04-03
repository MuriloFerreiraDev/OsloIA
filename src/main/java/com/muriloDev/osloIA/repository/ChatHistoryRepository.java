package com.muriloDev.osloIA.repository;

import com.muriloDev.osloIA.domain.model.ChatHistory;
import com.muriloDev.osloIA.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, UUID> {
    List<ChatHistory> findAllByUserOrderByCreatedAtDesc(User user);
}