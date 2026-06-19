package com.PBO2.CampShare.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.PBO2.CampShare.entity.ChatMessageEntity;
import com.PBO2.CampShare.repository.ChatRepository;

@RestController
@RequestMapping("/api/chat")
public class ChatHistoryController {
    private final ChatRepository chatRepository;

    @Autowired
    public ChatHistoryController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }
    @GetMapping("/history")
    public List<ChatMessageEntity> getChatHistory(@RequestParam String user1, @RequestParam String user2) {
        List<ChatMessageEntity> history = chatRepository.findBySenderAndRecipientOrSenderAndRecipientOrderByTimestampAsc(user1, user2, user2, user1);
        for (ChatMessageEntity msg : history) {
            if (msg.getRecipient().equals(user1) && !msg.isRead()) {
                msg.setRead(true);
                chatRepository.save(msg);
            }
        }
        return history;
    }

    @GetMapping("/contacts")
    public List<Map<String, Object>> getContacts(@RequestParam String myName) {
        List<String> partners = chatRepository.findChatPartners(myName);
        List<Map<String, Object>> contactList = new ArrayList<>();

        for (String partner : partners) {
            long unreadCount = chatRepository.countBySenderAndRecipientAndIsReadFalse(partner, myName);

            Map<String, Object> contactData = new HashMap<>();
            contactData.put("name", partner);
            contactData.put("unread", unreadCount);

            contactList.add(contactData);
        }

        return contactList;
    }
}
