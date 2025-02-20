package com.example.WalletApp;

import com.example.WalletApp.Controller.DepositController;
import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Currency;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DepositControllerTest {
    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;  // Mock UserService

    @InjectMocks
    private DepositController depositController;
    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(depositController)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

    }

    @Test
    public void testDeposit() throws Exception {
        Long userId = 1L;
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("INR"));

        doNothing().when(walletService).deposit(eq(userId), any(Money.class));

        mockMvc.perform(post("/users/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit successful!"));

        verify(walletService, times(1)).deposit(eq(userId), any(Money.class));
    }

    @Test
    public void testDeposit_ZeroAmount() throws Exception {
        Long userId = 1L;
        Money amount = new Money(BigDecimal.ZERO, Currency.getInstance("INR"));

        doThrow(new IllegalArgumentException("Amount must be greater than zero")).when(walletService).deposit(eq(userId), any(Money.class));

        mockMvc.perform(post("/users/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount must be greater than zero"));

        verify(walletService, times(1)).deposit(eq(userId), any(Money.class));
    }

    @Test
    public void testDeposit_NonExistentUser() throws Exception {
        Long userId = 999L;
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("INR"));

        doThrow(new IllegalArgumentException("User not found")).when(walletService).deposit(eq(userId), any(Money.class));

        mockMvc.perform(post("/users/999/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).deposit(eq(userId), any(Money.class));
    }
}
