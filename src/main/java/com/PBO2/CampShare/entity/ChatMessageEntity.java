package com.PBO2.CampShare.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "private_messages")
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead = false;

    public Long getId() { return id; }
    public void setId(Long id) {this.id = id; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
