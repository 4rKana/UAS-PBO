package com.PBO2.CampShare.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.PBO2.CampShare.dto.ChatMessageDTO;
import com.PBO2.CampShare.dto.ConversationDTO;
import com.PBO2.CampShare.dto.MessageRequest;
import com.PBO2.CampShare.dto.UnreadCountEvent;
import com.PBO2.CampShare.entity.Conversation;
import com.PBO2.CampShare.entity.ConversationReadStatus;
import com.PBO2.CampShare.entity.Message;
import com.PBO2.CampShare.entity.User;
import com.PBO2.CampShare.repository.ConversationReadStatusRepository;
import com.PBO2.CampShare.repository.ConversationRepository;
import com.PBO2.CampShare.repository.MessageRepository;
import com.PBO2.CampShare.repository.UserRepository;
import com.PBO2.CampShare.util.CryptoUtil;

@Service
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository; 
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CryptoUtil cryptoUtil;
    private final ConversationReadStatusRepository conversationReadStatusRepository;
    // private final UserRepository userRepository; // Inject jika ingin mengambil nama asli dari DB

    public ChatServiceImpl(ConversationRepository conversationRepository, MessageRepository messageRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate, CryptoUtil cryptoUtil, ConversationReadStatusRepository conversationReadStatusRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.cryptoUtil = cryptoUtil;
        this.conversationReadStatusRepository = conversationReadStatusRepository;
    }

    @Override
    public List<ConversationDTO> getChats(String userId) {
        List<Conversation> conversations = conversationRepository.findByUser1IdOrUser2Id(userId, userId);
        List<ConversationDTO> dtoList = new ArrayList<>();

        for (Conversation conv : conversations) {
            // 1. Tentukan ID lawan bicara
            String lawanBicaraId = conv.getUser1Id().equals(userId) ? conv.getUser2Id() : conv.getUser1Id();

            // 2. AMBIL NAMA ASLI DARI DATABASE (Menggunakan query yang sudah ada di repository)
            String namaLawan = conversationRepository.findNamaByUserId(lawanBicaraId);
            
            // Fallback jika nama tidak ditemukan atau null
            if (namaLawan == null || namaLawan.isEmpty()) {
                namaLawan = lawanBicaraId; 
            }

            // 3. Ambil pesan terakhir (beserta waktunya, untuk timestamp ala WhatsApp)
            List<Message> messages = messageRepository.findByConversationIdAndIsDeletedFalseOrderByCreatedAtAsc(conv.getId());
            String pesanTerakhir;
            LocalDateTime waktuPesanTerakhir = null;

            if (messages.isEmpty()) {
                pesanTerakhir = "Belum ada pesan";
            } else {
                Message lastMsg = messages.get(messages.size() - 1);
                pesanTerakhir = cryptoUtil.decrypt(lastMsg.getMessage());
                waktuPesanTerakhir = lastMsg.getCreatedAt();
            }

            // 4. Hitung jumlah pesan belum dibaca KHUSUS conversation ini
            //    (untuk titik merah pada avatar item chat list)
            long unreadCount = messageRepository.countUnreadMessagesInConversation(conv.getId(), userId);

            // 5. Masukkan ke DTO dengan nama yang sudah benar
            dtoList.add(new ConversationDTO(conv.getId(), namaLawan, pesanTerakhir, waktuPesanTerakhir, unreadCount));
        }

        // Urutkan berdasarkan waktu pesan TERAKHIR, paling baru di atas (standar WhatsApp).
        // Conversation yang belum punya pesan sama sekali (lastMessageAt == null)
        // ditempatkan paling bawah, bukan di atas.
        dtoList.sort((a, b) -> {
            if (a.getLastMessageAt() == null && b.getLastMessageAt() == null) return 0;
            if (a.getLastMessageAt() == null) return 1;  // a kosong -> a di bawah
            if (b.getLastMessageAt() == null) return -1; // b kosong -> b di bawah
            return b.getLastMessageAt().compareTo(a.getLastMessageAt()); // descending (terbaru dulu)
        });

        return dtoList;
    }

    @Override
    public List<Message> getMessages(Integer conversationId) {
        List<Message> messages = messageRepository.findByConversationIdAndIsDeletedFalseOrderByCreatedAtAsc(conversationId);

        // Dekripsi setiap pesan sebelum dikembalikan ke controller/frontend.
        // Catatan: ini memodifikasi objek Message hasil query (bukan menulis
        // balik ke DB — perubahan ini tidak akan tersimpan karena tidak ada
        // pemanggilan save() lagi setelah ini).
        for (Message msg : messages) {
            msg.setMessage(cryptoUtil.decrypt(msg.getMessage()));
        }

        return messages;
    }

    @Override
    public void sendMessage(MessageRequest request) {
        Message message = new Message();
        message.setConversationId(request.getConversationId());
        message.setSenderId(request.getSenderId());
        // Simpan dalam bentuk terenkripsi (encryption at rest) — yang masuk ke
        // database adalah ciphertext, bukan teks asli.
        message.setMessage(cryptoUtil.encrypt(request.getMessage()));
        message.setCreatedAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);

        // Broadcast pesan ke semua client yang sedang subscribe ke conversation ini,
        // sehingga penerima (dan pengirim) langsung melihat pesan baru tanpa refresh.
        // Catatan: yang dibroadcast adalah PLAINTEXT asli (request.getMessage()),
        // bukan hasil cryptoUtil.encrypt(), karena browser butuh teks yang bisa
        // langsung ditampilkan apa adanya.
        ChatMessageDTO payload = new ChatMessageDTO(
                saved.getId(),
                saved.getConversationId(),
                saved.getSenderId(),
                request.getMessage(),
                saved.getCreatedAt()
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + saved.getConversationId(),
                payload
        );

        // Beritahu PENERIMA pesan (bukan pengirim) bahwa jumlah pesan belum
        // dibacanya bertambah — supaya titik merah indikator chat di halaman
        // LAIN (misal notifikasi.html) bisa langsung update real-time, tanpa
        // perlu reload halaman atau polling berkala.
        conversationRepository.findById(saved.getConversationId()).ifPresent(conv -> {
            String penerimaId = conv.getUser1Id().equals(saved.getSenderId())
                    ? conv.getUser2Id()
                    : conv.getUser1Id();

            long unreadCountPenerima = messageRepository.countUnreadMessages(penerimaId);

            messagingTemplate.convertAndSend(
                    "/topic/unread/" + penerimaId,
                    new UnreadCountEvent(unreadCountPenerima)
            );

            // Beritahu KEDUA pihak (pengirim & penerima) bahwa ada aktivitas baru
            // di salah satu conversation mereka, supaya chat list di sebelah kiri
            // langsung reorder & update preview secara real-time — terlepas dari
            // conversation mana yang sedang dibuka saat ini. Ini topic GLOBAL milik
            // tiap user (bukan per-conversation seperti /topic/chat/{id}), sehingga
            // tidak perlu subscribe/unsubscribe berulang setiap pindah chat.
            messagingTemplate.convertAndSend("/topic/chatlist/" + saved.getSenderId(), "refresh");
            messagingTemplate.convertAndSend("/topic/chatlist/" + penerimaId, "refresh");
        });
    }

    @Override
    public ConversationDTO startConversation(String currentUserId, String targetUsername) {
        // 0. Cari id_user asli berdasarkan username yang diketik di kotak pencarian.
        //    (Sebelumnya kode ini langsung memakai targetUsername sebagai ID,
        //    padahal yang diketik user adalah username, bukan id_user — itu sebabnya
        //    pencarian selalu gagal dengan "User tidak ditemukan".)
        String targetUserId = conversationRepository.findUserIdByUsername(targetUsername);

        if (targetUserId == null || targetUserId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username tidak ditemukan!");
        }

        if (currentUserId.equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tidak bisa chat dengan diri sendiri!");
        }

        // 1. Validasi keberadaan user target di database (jaga-jaga / defensif,
        //    sebenarnya sudah pasti ada karena baru ditemukan lewat username di atas)
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
        List<Message> messages = messageRepository.findByConversationIdAndIsDeletedFalseOrderByCreatedAtAsc(conv.getId());
        String lastMsg;
        LocalDateTime lastMsgAt = null;

        if (messages.isEmpty()) {
            lastMsg = "Belum ada pesan";
        } else {
            Message lastMessage = messages.get(messages.size() - 1);
            lastMsg = cryptoUtil.decrypt(lastMessage.getMessage());
            lastMsgAt = lastMessage.getCreatedAt();
        }

        long unreadCount = messageRepository.countUnreadMessagesInConversation(conv.getId(), currentUserId);

        return new ConversationDTO(conv.getId(), namaLawan, lastMsg, lastMsgAt, unreadCount);
    }

    @Override
    public long getUnreadMessageCount(String userId) {
        return messageRepository.countUnreadMessages(userId);
    }

@Override
    public void markConversationAsRead(Integer conversationId, String userId) {
        try {
            ConversationReadStatus status = conversationReadStatusRepository
                    .findByConversationIdAndUserId(conversationId, userId)
                    .orElseGet(() -> {
                        ConversationReadStatus newStatus = new ConversationReadStatus();
                        newStatus.setConversationId(conversationId);
                        newStatus.setUserId(userId);
                        return newStatus;
                    });

            status.setLastReadAt(LocalDateTime.now());
            conversationReadStatusRepository.save(status);

            // Beritahu user ini bahwa jumlah pesan belum dibacanya sudah berubah
            long unreadCountTerbaru = messageRepository.countUnreadMessages(userId);

            messagingTemplate.convertAndSend(
                    "/topic/unread/" + userId,
                    new UnreadCountEvent(unreadCountTerbaru)
            );
        } catch (Exception e) {
            // JIKA DATABASE ERROR/CRASH, PRINT KE TERMINAL IDE AGAR KITA TAHU KENAPA:
            System.err.println("=== LOG WARNING: Gagal menandai pesan terbaca di DB ===");
            e.printStackTrace();
            
            // PENTING: Sengaja di-catch tanpa melempar Error 500,
            // agar API di browser tetap mendapat status 200 OK.
            // Dengan begitu, JavaScript tidak macet & WebSocket langsung tersambung otomatis!
        }
    }

    public User findByUsername(String username) {
        // Memanggil query ke database melalui UserRepository
        return userRepository.findByUsername(username);
    }
}