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
        UserResponse userResponse = new UserResponse(1L, "testUser", new Money(new BigDecimal("100.00")));
        when(userService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"username\":\"testUser\",\"walletBalance\":{\"amount\":100.00}}"));

        verify(userService, times(1)).getUserById(1L);
    }


    @Test
    public void testGetUserByUsername() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "testUser", new Money(new BigDecimal("100.00")));
        when(userService.getUserByUsername("testUser")).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/by-username/testUser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"username\":\"testUser\",\"walletBalance\":{\"amount\":100.00}}"));

        verify(userService, times(1)).getUserByUsername("testUser");
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
    public void testGetUserByNonExistentUsername() throws Exception {
        when(userService.getUserByUsername("nonExistentUser")).thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(get("/api/users/by-username/nonExistentUser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userService, times(1)).getUserByUsername("nonExistentUser");
    }
}
