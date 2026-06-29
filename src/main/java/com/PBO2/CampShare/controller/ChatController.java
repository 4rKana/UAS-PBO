package com.PBO2.CampShare.controller;

import java.util.List;
import java.util.Map;

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

    // PENTING: path statis seperti "/unread-count/{userId}" HARUS dideklarasikan
    // SEBELUM "/{userId}" di bawah, supaya Spring tidak salah mencocokkan
    // "/unread-count/xxx" sebagai getChats dengan userId="unread-count".
    // (Spring sebenarnya cukup pintar membedakan path statis vs variabel,
    // tapi urutan ini tetap dijaga agar tidak ambigu dan mudah dibaca.)

    // Jumlah pesan belum dibaca milik seorang user, lintas semua conversation
    // (dipakai untuk titik merah pada tombol chat di header)
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(@PathVariable String userId) {
        long count = chatService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    // Menandai sebuah conversation sebagai sudah dibaca oleh seorang user
    // (dipanggil dari frontend setiap kali user membuka sebuah percakapan)
    @PostMapping("/read/{conversationId}")
    public ResponseEntity<?> markConversationAsRead(
            @PathVariable Integer conversationId,
            @RequestParam String userId) {
        chatService.markConversationAsRead(conversationId, userId);
        return ResponseEntity.ok().build();
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