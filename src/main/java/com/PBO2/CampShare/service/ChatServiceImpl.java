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
import com.PBO2.CampShare.entity.Conversation;
import com.PBO2.CampShare.entity.Message;
import com.PBO2.CampShare.entity.User;
import com.PBO2.CampShare.repository.ConversationRepository;
import com.PBO2.CampShare.repository.MessageRepository;
import com.PBO2.CampShare.util.CryptoUtil;
// import com.PBO2.CampShare.repository.UserRepository; // Pastikan diimport jika ada
import com.PBO2.CampShare.repository.UserRepository;

@Service
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository; 
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CryptoUtil cryptoUtil;
    // private final UserRepository userRepository; // Inject jika ingin mengambil nama asli dari DB

    public ChatServiceImpl(ConversationRepository conversationRepository, MessageRepository messageRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate, CryptoUtil cryptoUtil) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.cryptoUtil = cryptoUtil;
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

            // 3. Ambil pesan terakhir
            List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId());
            String pesanTerakhir = messages.isEmpty()
                    ? "Belum ada pesan"
                    : cryptoUtil.decrypt(messages.get(messages.size() - 1).getMessage());

            // 4. Masukkan ke DTO dengan nama yang sudah benar
            dtoList.add(new ConversationDTO(conv.getId(), namaLawan, pesanTerakhir));
        }

        return dtoList;
    }

    @Override
    public List<Message> getMessages(Integer conversationId) {
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

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
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId());
        String lastMsg = messages.isEmpty()
                ? "Belum ada pesan"
                : cryptoUtil.decrypt(messages.get(messages.size() - 1).getMessage());

        return new ConversationDTO(conv.getId(), namaLawan, lastMsg);
    }

    public User findByUsername(String username) {
        // Memanggil query ke database melalui UserRepository
        return userRepository.findByUsername(username);
    }
}