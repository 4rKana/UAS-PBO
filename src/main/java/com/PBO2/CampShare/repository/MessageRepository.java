package com.PBO2.CampShare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.PBO2.CampShare.entity.Message;

public interface MessageRepository
        extends JpaRepository<Message, Integer> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(
            Integer conversationId);
}