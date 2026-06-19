package com.PBO2.CampShare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.PBO2.CampShare.entity.ChatMessageEntity;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findBySenderAndRecipientOrSenderAndRecipientOrderByTimestampAsc(
        String sender1, String recipient1, String sender2, String recipient2
    );

    @Query("SELECT DISTINCT CASE WHEN c.sender = :user THEN c.recipient ELSE c.sender END " +
    " FROM ChatMessageEntity c Where c.sender = :user OR c.recipient = :user")
    List<String> findChatPartners(@Param("user")String user);

    long countBySenderAndRecipientAndIsReadFalse(String sender, String recipient);
}
