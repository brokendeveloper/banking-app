package com.brokendev.backend.notification.controller;

import com.brokendev.backend.common.domain.user.User;
import com.brokendev.backend.common.domain.user.UserRepository;
import com.brokendev.backend.notification.domain.Notification;
import com.brokendev.backend.notification.domain.NotificationRepository;
import com.brokendev.backend.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TokenService tokenService;

    private String validToken;
    private User testUser;
    private Notification unreadNotification;

    @BeforeEach
    void setUp() {

        notificationRepository.deleteAll();
        userRepository.deleteAll();


        testUser = new User();
        testUser.setName("Luccas Notifications");
        testUser.setEmail("luccas.notif@email.com");
        testUser.setCpf("55544433322");
        testUser.setPassword("senhaSegura123");
        testUser.setTelephone("81944444444");
        userRepository.save(testUser);


        unreadNotification = new Notification();
        unreadNotification.setUser(testUser);
        unreadNotification.setTitle("Welcome!");
        unreadNotification.setMessage("Your account has been created.");
        unreadNotification.setCreatedAt(LocalDateTime.now());
        unreadNotification.setRead(false);
        notificationRepository.save(unreadNotification);


        validToken = tokenService.generateToken(testUser);
    }

    @Test
    @Transactional
    void shouldListNotificationsSuccessfully() throws Exception {

        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Welcome!"))
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    @Transactional
    void shouldMarkNotificationAsReadSuccessfully() throws Exception {

        mockMvc.perform(patch("/api/notifications/" + unreadNotification.getId() + "/read")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isNoContent()); // 204 No Content


        Notification updatedNotification = notificationRepository.findById(unreadNotification.getId()).orElseThrow();
        assertTrue(updatedNotification.isRead());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());
    }
}