package com.homework.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.task.database.services.TokenBlacklistService;
import com.homework.task.requests.UserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTests {

    private static final String BASE_URL = "http://localhost:8080";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final UserRequest basicUserRequest = new UserRequest("user", "user");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;


    @AfterEach
    void resetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @AfterEach
    void resetMocks() {
        Mockito.reset(tokenBlacklistService); // Reset mock state
    }

    private void registerBasicUser() throws Exception {
        mockMvc.perform(post(BASE_URL + "/register")
                        .content(objectMapper.writeValueAsString(basicUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private String loginBasicUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/login")
                        .content(objectMapper.writeValueAsString(basicUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return mvcResult.getResponse().getContentAsString();
    }

    @Test
    void registerUser() throws Exception {

        registerBasicUser();

    }

    @Test
    void registerUserThatAlreadyExists() throws Exception {

        registerBasicUser();

        mockMvc.perform(post(BASE_URL + "/register")
                        .content(objectMapper.writeValueAsString(basicUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void loginWithExistingUser() throws Exception {

        registerBasicUser();

        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/login")
                        .content(objectMapper.writeValueAsString(basicUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).contains("bearer");

    }

    @Test
    void loginWithNonExistingUser() throws Exception {

        mockMvc.perform(post(BASE_URL + "/login")
                        .content(objectMapper.writeValueAsString(basicUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void logoutWithExistingUser() throws Exception {

        registerBasicUser();
        String token = loginBasicUser().split(":")[1];

        mockMvc.perform(post(BASE_URL + "/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(true);

        mockMvc.perform(post(BASE_URL + "/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void logoutWithoutAuthentication() throws Exception {

        mockMvc.perform(post(BASE_URL + "/logout")
                        .content(objectMapper.writeValueAsString(basicUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());


    }

    @Test
    void logoutWithWrongToken() throws Exception {

        mockMvc.perform(post(BASE_URL + "/logout")
                        .content(objectMapper.writeValueAsString(basicUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authentication", "bearer thistokenlookscool")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());


    }

    @Test
    void logoutWithoutToken() throws Exception {

        mockMvc.perform(post(BASE_URL + "/logout")
                        .content(objectMapper.writeValueAsString(basicUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authentication", "bearer ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());


    }

}
