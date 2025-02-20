package com.example.WalletApp;

import com.example.WalletApp.Controller.UserController;
import com.example.WalletApp.DTO.UserResponse;
import com.example.WalletApp.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.WalletApp.Domain.Money;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Currency;


public class UserControllerTest {

    //@Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;  // Mock UserService

    @InjectMocks
    private UserController userController;
    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .build();
    }

    @Test
    public void testRegisterUser() throws Exception {
        doNothing().when(userService).registerUser("testUser", "securePassword");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"password\": \"securePassword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));

        verify(userService, times(1)).registerUser("testUser", "securePassword");
    }

    @Test
    public void testGetUserById() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "testUser",
                new Money(new BigDecimal("100.00"), Currency.getInstance("INR")));
        userResponse.setMessage("Success");

        when(userService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.walletBalance.amount").value(100.00))
                .andExpect(jsonPath("$.walletBalance.currency").value("INR"))
                .andExpect(jsonPath("$.message").value("Success"));  //Fix

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    public void testRegisterUserWithEmptyUsername() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"\", \"password\": \"securePassword\"}"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(anyString(), anyString());
    }
    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        when(userService.authenticateUser("testUser", "wrongPassword")).thenReturn(false);

        mockMvc.perform(post("/api/users/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"password\": \"wrongPassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));

        verify(userService, times(1)).authenticateUser("testUser", "wrongPassword");
    }

    @Test
    public void testGetUserWithEmptyWallet() throws Exception {
        UserResponse userResponse = new UserResponse(2L, "emptyWalletUser", new Money(BigDecimal.ZERO, Currency.getInstance("INR")));

        when(userService.getUserById(2L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletBalance.amount").value(0.00))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(userService, times(1)).getUserById(2L);
    }

    @Test
    public void testMultipleRequests() throws Exception {
        UserResponse userResponse = new UserResponse(4L, "multiRequestUser", new Money(new BigDecimal("200.00"), Currency.getInstance("INR")));

        when(userService.getUserById(4L)).thenReturn(userResponse);

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/users/4")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("multiRequestUser"))
                    .andExpect(jsonPath("$.walletBalance.amount").value(200.00));
        }

        verify(userService, times(5)).getUserById(4L);
    }
    @Test
    public void testGetUserByNonExistentId() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(get("/api/users/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userService, times(1)).getUserById(999L);
    }

}

