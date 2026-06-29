package com.PBO2.CampShare.service;

import com.PBO2.CampShare.dto.NotificationDTO;
import com.PBO2.CampShare.entity.Notification;
import com.PBO2.CampShare.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void createNotification(String userId, String title, String message, String type, String referenceId) {
        Notification notif = new Notification();
        notif.setUserId(userId);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setType(type);
        notif.setReferenceId(referenceId);
        notif.setRead(false);
        notif.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notif);
    }

    @Override
    public List<NotificationDTO> getNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(n -> new NotificationDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.getType(),
                        n.getReferenceId(),
                        n.isRead(),
                        n.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notifikasi tidak ditemukan"));

        notif.setRead(true);
        notificationRepository.save(notif);
    }

    @Override
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}