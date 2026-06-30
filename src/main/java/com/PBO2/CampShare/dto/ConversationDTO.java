package com.PBO2.CampShare.dto;

import com.fasterxml.jackson.annotation.JsonFormat; // <-- TAMBAHKAN IMPORT INI
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

    // Tambahkan anotasi ini agar waktu pesan terakhir tidak dikirim sebagai array:
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastMessageAt; 
    
    private long unreadCount; 

}