package com.PBO2.CampShare.repository;

import com.PBO2.CampShare.entity.ConversationReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationReadStatusRepository extends JpaRepository<ConversationReadStatus, Long> {

    Optional<ConversationReadStatus> findByConversationIdAndUserId(Integer conversationId, String userId);
}