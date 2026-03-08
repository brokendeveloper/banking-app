package com.brokendev.backend.auth.controller;

import com.brokendev.backend.auth.dto.login.LoginRequestDTO;
import com.brokendev.backend.auth.dto.register.RegisterRequestDTO;
import com.fasterxml.jackson.databind.JsonNode; // Novo import
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional; // Novo import
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult; // Novo import

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequestDTO validUser;

    @BeforeEach
    void setUp() {
        validUser = new RegisterRequestDTO(
                "Test User",
                "12345678900",
                "test@email.com",
                "strongPassword123",
                "81912345678"
        );
    }


    private void registerUser(RegisterRequestDTO userToRegister) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToRegister)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    private String loginUserAndGetToken(LoginRequestDTO loginRequest) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode jsonResponse = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonResponse.get("token").asText();
    }

    // register tests
    @Test
    @Transactional
    void shouldRegisterUserSuccessfully() throws Exception {
        registerUser(validUser);
    }

    @Test
    @Transactional
    void shouldNotRegisterUserWhenEmailAlreadyExists() throws Exception {
        registerUser(validUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    @Transactional
    void shouldNotRegisterUserWhenRegisteringWithInvalidData() throws Exception { // Typo corrected
        RegisterRequestDTO invalidRequest = new RegisterRequestDTO(
                "",
                "abc",
                "email",
                "123",
                "telefone"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // login tests
    @Test
    @Transactional
    void shouldLoginSuccessfullyWhenUserExists() throws Exception {
        registerUser(validUser);
        LoginRequestDTO loginRequest = new LoginRequestDTO(validUser.email(), validUser.password());

        loginUserAndGetToken(loginRequest);
    }

    @Test
    @Transactional
    void shouldNotLoginSuccessfullyWhenUserInvalidPassword() throws Exception {
        registerUser(validUser); // Garante que o usuário existe
        LoginRequestDTO invalidLogin = new LoginRequestDTO(validUser.email(), "wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid password"));
    }

    @Test
    @Transactional
    void shouldNotLoginSuccessfullyWhenUserDoesNotExist() throws Exception {
        LoginRequestDTO nonExistentLogin = new LoginRequestDTO("nonexistent@email.com", "anypassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentLogin)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @Transactional
    void shouldNotAccessProtectedResourceWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/profile/verify")) // <-- ROTA ATUALIZADA
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void shouldAccessProtectedResourceWithValidToken() throws Exception {
        registerUser(validUser);
        LoginRequestDTO loginRequest = new LoginRequestDTO(validUser.email(), validUser.password());
        String token = loginUserAndGetToken(loginRequest);


        mockMvc.perform(get("/api/profile/verify") // <-- ROTA ATUALIZADA
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void shouldNotAccessProtectedResourceWithInvalidToken() throws Exception {
        String invalidToken = "invalid.jwt.token.string";

        mockMvc.perform(get("/api/profile/verify") // <-- ROTA ATUALIZADA
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }
}