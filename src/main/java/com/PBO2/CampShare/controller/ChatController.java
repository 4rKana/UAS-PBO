package com.PBO2.CampShare.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.PBO2.CampShare.entity.ChatMessageEntity;
import com.PBO2.CampShare.repository.ChatRepository;

@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatRepository chatRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatRepository = chatRepository;
    }

    @MessageMapping("/private-message")
    public void handlePrivateMessage(ChatMessageEntity message) {
        message.setTimestamp(LocalDateTime.now());

        chatRepository.save(message);

        messagingTemplate.convertAndSend("/queue/messages/" + message.getRecipient(), message);

        messagingTemplate.convertAndSend("/queue/messages/" + message.getSender(), message);
    }
}
