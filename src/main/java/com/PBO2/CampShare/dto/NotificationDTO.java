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
public class NotificationDTO {

    private Long id;
    private String title;
    private String message;
    private String type;
    private String referenceId;
    private boolean isRead;
    private LocalDateTime createdAt;
}