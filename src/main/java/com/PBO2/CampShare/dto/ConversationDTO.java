package com.PBO2.CampShare.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {

    private Integer id;
    private String nama;
    private String lastMessage;
    private LocalDateTime lastMessageAt; // Waktu pesan terakhir, untuk format timestamp ala WhatsApp di chat list
    private long unreadCount; // Jumlah pesan belum dibaca KHUSUS conversation ini, untuk titik merah di avatar

}