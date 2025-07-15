package com.api.authapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthTest {

   /* @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .email("existing_email@gmail.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Test
    public void register_ShouldReturnCreated_WhenDataIsCorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"email@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isCreated());
    }

    @Test
    public void register_ShouldReturnConflict_WhenUserAlreadyExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"existing_email@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isConflict());
    }

    @Test
    public void login_ShouldReturnOk_WhenDataIsCorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"existing_email@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isOk());
    }

    @Test
    public void login_ShouldReturnUnauthorized_WhenPasswordIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"existing_email@gmail.com\", \"password\": \"incorrect_password\" }"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_ShouldReturnUnauthorized_WhenEmailIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"incorrect_email@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logout_ShouldReturnNoContent_WhenTokenIsCorrect() throws Exception {
        String loggedInData = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"existing_email@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = new ObjectMapper()
                .readTree(loggedInData)
                .get("token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    public void refresh_ShouldReturnOk_WhenRefreshTokenIsValid() throws Exception {
        String loggedInData = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"existing_email@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = new ObjectMapper()
                .readTree(loggedInData)
                .get("token")
                .asText();
        String refreshToken = new ObjectMapper()
                .readTree(loggedInData)
                .get("refreshToken")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh")
                        .header("Authorization", "Bearer " + token)
                        .content(String.format("{\"refreshToken\": \"%s\"}", refreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void refresh_ShouldReturnUnauthorized_WhenRefreshTokenIsInvalid() throws Exception {
        String loggedInData = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"existing_email@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = new ObjectMapper()
                .readTree(loggedInData)
                .get("token")
                .asText();
        String refreshToken = "fake_refresh_token";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh")
                        .header("Authorization", "Bearer " + token)
                        .content(String.format("{\"refreshToken\": \"%s\"}", refreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUser_ShouldReturnOk_WhenDataIsCorrect() throws Exception {
        String loggedInData = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"existing_email@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = new ObjectMapper()
                .readTree(loggedInData)
                .get("token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/auth")
                        .header("Authorization", "Bearer " + token)
                        .content(String.format("{\"lastPassword\": \"%s\", \"newPassword\": \"%s\"}", "password", "newPassword"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_ShouldReturnOk_WhenLastPasswordIsIncorrect() throws Exception {
        String loggedInData = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"existing_email@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = new ObjectMapper()
                .readTree(loggedInData)
                .get("token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/auth")
                        .header("Authorization", "Bearer " + token)
                        .content(String.format("{\"lastPassword\": \"%s\", \"newPassword\": \"%s\"}", "incorrect_password", "newPassword"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteUser_ShouldReturnOk_WhenDataIsCorrect() throws Exception {
        String loggedInData = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"existing_email@gmail.com\", \"password\": \"password\" }"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = new ObjectMapper()
                .readTree(loggedInData)
                .get("token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/auth")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }*/
}
