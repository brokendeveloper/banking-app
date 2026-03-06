package com.brokendev.backend.notification.service;

import com.brokendev.backend.notification.domain.Notification;
import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.notification.dto.NotificationResponseDTO;
import com.brokendev.backend.common.exceptions.NotificationAccessDeniedException;
import com.brokendev.backend.common.exceptions.NotificationNotFoundException;
import com.brokendev.backend.notification.domain.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void notify(User user, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        notificationRepository.save(notification);

        // Simulação de envio real (log)
        System.out.printf("Notificação para %s: %s - %s%n", user.getEmail(), title, message);

    }

    public List<NotificationResponseDTO> listNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(n -> new NotificationResponseDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.getCreatedAt(),
                        n.isRead()
                ))
                .toList();
    }

    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notificação não encontrada"));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new NotificationAccessDeniedException("Acesso negado à notificação");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}

