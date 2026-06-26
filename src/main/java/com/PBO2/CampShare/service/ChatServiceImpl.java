package com.PBO2.CampShare.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.PBO2.CampShare.dto.ConversationDTO;
import com.PBO2.CampShare.dto.MessageRequest;
import com.PBO2.CampShare.entity.Conversation;
import com.PBO2.CampShare.entity.Message;
import com.PBO2.CampShare.entity.User;
import com.PBO2.CampShare.repository.ConversationRepository;
import com.PBO2.CampShare.repository.MessageRepository;
// import com.PBO2.CampShare.repository.UserRepository; // Pastikan diimport jika ada
import com.PBO2.CampShare.repository.UserRepository;

@Service
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository; 
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    // private final UserRepository userRepository; // Inject jika ingin mengambil nama asli dari DB

    public ChatServiceImpl(ConversationRepository conversationRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ConversationDTO> getChats(String userId) {
        // 1. Ambil data asli conversation dari database
        List<Conversation> conversations = conversationRepository.findByUser1IdOrUser2Id(userId, userId);
        List<ConversationDTO> dtoList = new ArrayList<>();

        for (Conversation conv : conversations) {
            // 2. Tentukan ID lawan bicara (siapa yang bukan diri kita sendiri)
            String lawanBicaraId = conv.getUser1Id().equals(userId) ? conv.getUser2Id() : conv.getUser1Id();

            // 3. Ambil nama lawan bicara dari tabel users (Contoh simulasi jika menggunakan userRepository)
            // String namaLawan = userRepository.findNamaById(lawanBicaraId); 
            String namaLawan = "User (" + lawanBicaraId + ")"; // Fallback sementara jika query user belum dibuat

            // 4. Ambil pesan terakhir dari conversation ini
            List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId());
            String pesanTerakhir = "";
            if (!messages.isEmpty()) {
                pesanTerakhir = messages.get(messages.size() - 1).getMessage();
            }

            // 5. Masukkan ke dalam DTO
            dtoList.add(new ConversationDTO(conv.getId(), namaLawan, pesanTerakhir));
        }

        return dtoList;
    }

    @Override
    public List<Message> getMessages(Integer conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Override
    public void sendMessage(MessageRequest request) {
        Message message = new Message();
        message.setConversationId(request.getConversationId());
        message.setSenderId(request.getSenderId());
        message.setMessage(request.getMessage());
        // Jika kolom created_at tidak auto-isi di DB, tambahkan:
        // message.setCreatedAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    @Override
    public ConversationDTO startConversation(String currentUserId, String targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tidak bisa chat dengan diri sendiri!");
        }

        // 1. Validasi keberadaan user target di database
        if (conversationRepository.checkUserExists(targetUserId) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID tidak ditemukan!");
        }

        // 2. Cek apakah room chat sudah ada, jika belum maka buat baru
        Optional<Conversation> existing = conversationRepository.findConversationBetween(currentUserId, targetUserId);
        Conversation conv;
        
        if (existing.isPresent()) {
            conv = existing.get();
        } else {
            conv = new Conversation();
            conv.setUser1Id(currentUserId);
            conv.setUser2Id(targetUserId);
            conv.setCreatedAt(LocalDateTime.now());
            conv = conversationRepository.save(conv);
        }

        // 3. Ambil nama lawan bicara untuk dikembalikan ke Frontend
        String namaLawan = conversationRepository.findNamaByUserId(targetUserId);
        if (namaLawan == null || namaLawan.isEmpty()) {
            namaLawan = targetUserId; // Fallback jika kolom nama kosong
        }

        // 4. Ambil pesan terakhir (kosong jika obrolan baru)
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId());
        String lastMsg = messages.isEmpty() ? "Belum ada pesan" : messages.get(messages.size() - 1).getMessage();

        return new ConversationDTO(conv.getId(), namaLawan, lastMsg);
    }

    public User findByUsername(String username) {
        // Memanggil query ke database melalui UserRepository
        return userRepository.findByUsername(username);
    }
}