package com.PBO2.CampShare.service;

import java.util.List;

import com.PBO2.CampShare.dto.MessageRequest;
import com.PBO2.CampShare.dto.ConversationDTO; // Tambahkan import DTO
import com.PBO2.CampShare.entity.Message;

public interface ChatService {

    ConversationDTO startConversation(String currentUserId, String targetUsername);

    List<ConversationDTO> getChats(String userId); // Ubah dari Conversation ke ConversationDTO

    List<Message> getMessages(Integer conversationId);

    void sendMessage(MessageRequest request);

    // Jumlah pesan belum dibaca milik seorang user, lintas semua conversation
    // (dipakai untuk titik merah pada tombol chat)
    long getUnreadMessageCount(String userId);

    // Menandai sebuah conversation sebagai "sudah dibaca" oleh seorang user
    // sampai waktu saat ini (dipanggil saat user membuka percakapan tersebut)
    void markConversationAsRead(Integer conversationId, String userId);
}