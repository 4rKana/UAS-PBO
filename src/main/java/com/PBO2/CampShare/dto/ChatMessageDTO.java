package com.PBO2.CampShare.dto;

import com.fasterxml.jackson.annotation.JsonFormat; // <-- TAMBAHKAN IMPORT INI
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Payload yang di-broadcast lewat WebSocket ke topic /topic/chat/{conversationId}.
 * Sengaja dibuat terpisah dari entity Message agar bebas menambah field
 * (misal "nama" pengirim) tanpa mengubah struktur tabel/entity database.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    private Integer id;
    private Integer conversationId;
    private String senderId;
    private String message;

    // Tambahkan anotasi ini agar dikirim sebagai String ISO standar:
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}