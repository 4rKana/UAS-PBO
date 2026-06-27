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
}