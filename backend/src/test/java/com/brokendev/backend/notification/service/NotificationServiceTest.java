package com.brokendev.backend.notification.service;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.exceptions.NotificationAccessDeniedException;
import com.brokendev.backend.common.exceptions.NotificationNotFoundException;
import com.brokendev.backend.notification.domain.Notification;
import com.brokendev.backend.notification.domain.NotificationRepository;
import com.brokendev.backend.notification.dto.NotificationResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");

        notification = new Notification();
        notification.setId(100L);
        notification.setUser(user);
        notification.setTitle("Title");
        notification.setMessage("Message");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
    }

    @Test
    void notify_givenValidUserAndMessage_whenCalled_thenSaveNotification() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.notify(user, "Title", "Message");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void listNotifications_givenUserWithNotifications_whenCalled_thenReturnList() {
        when(notificationRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(notification));

        List<NotificationResponseDTO> list = notificationService.listNotifications(user);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).id()).isEqualTo(100L);
        assertThat(list.get(0).title()).isEqualTo("Title");
        assertThat(list.get(0).read()).isFalse();
    }

    @Test
    void markAsRead_givenValidNotificationIdAndUser_whenOwnershipValid_thenMarkAsRead() {
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.markAsRead(100L, user);

        assertThat(notification.isRead()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_givenInvalidNotificationId_whenNotFound_thenThrowException() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(999L, user))
                .isInstanceOf(NotificationNotFoundException.class)
                .hasMessageContaining("Notification not found");
    }

    @Test
    void markAsRead_givenNotificationOfOtherUser_whenOwnershipInvalid_thenThrowException() {
        User otherUser = new User();
        otherUser.setId(2L);
        notification.setUser(otherUser);

        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.markAsRead(100L, user))
                .isInstanceOf(NotificationAccessDeniedException.class)
                .hasMessageContaining("Access to notification denied"); // <-- AJUSTE AQUI
    }
}