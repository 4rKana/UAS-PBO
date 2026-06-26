package com.PBO2.CampShare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.PBO2.CampShare.dto.ConversationDTO;
import com.PBO2.CampShare.dto.MessageRequest;
import com.PBO2.CampShare.entity.Conversation;
import com.PBO2.CampShare.entity.Message;
import com.PBO2.CampShare.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{userId}")
    public List<ConversationDTO> getChats(@PathVariable String userId) {
        return chatService.getChats(userId);
    }

    @GetMapping("/messages/{conversationId}")
    public List<Message> getMessages(
            @PathVariable Integer conversationId) {

        return chatService.getMessages(
                conversationId);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestBody MessageRequest request) {

        chatService.sendMessage(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/start")
    public ResponseEntity<?> startConversation(
            @RequestParam String currentUserId, 
            @RequestParam String targetUsername) {
        try {
            ConversationDTO dto = chatService.startConversation(currentUserId, targetUsername);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}