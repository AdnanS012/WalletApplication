package com.example.WalletApp;


import com.example.WalletApp.Controller.WalletController;
import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerTest {
    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;  // Mock UserService

    @InjectMocks
    private WalletController walletController;
    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController)
                .build();
    }

    @Test
    public void testDepositToWallet() throws Exception {
        Long userId = 1L;
        doNothing().when(walletService).deposit(eq(userId), any(Money.class));

        mockMvc.perform(post("/api/wallets/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100.00}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit successful!"));

        verify(walletService, times(1)).deposit(eq(userId), any(Money.class));
    }

    @Test
    public void testWithdrawFromWalletSuccess() throws Exception {
        Long userId = 1L;
        Money withdrawAmount = new Money(new BigDecimal("50.00"));

        doNothing().when(walletService).withdraw(eq(userId), any(Money.class));

        mockMvc.perform(post("/api/wallets/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdrawal successful!"));

        verify(walletService, times(1)).withdraw(eq(userId), any(Money.class));
    }

    @Test
    public void testWithdrawFromWallet_InsufficientBalance() throws Exception {
        Long userId = 1L;
        Money withdrawAmount = new Money(new BigDecimal("500.00"));

        doThrow(new IllegalArgumentException("Insufficient balance")).when(walletService).withdraw(eq(userId), any(Money.class));

        mockMvc.perform(post("/api/wallets/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 500.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));

        verify(walletService, times(1)).withdraw(eq(userId), any(Money.class));
    }
    @Test
    public void testGetWalletBalance() throws Exception {
        Long userId = 1L;
        Money balance = new Money(new BigDecimal("200.00"));

        when(walletService.getBalance(eq(userId))).thenReturn(balance);

        mockMvc.perform(get("/api/wallets/1/balance")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"amount\": 200.00}"));

        verify(walletService, times(1)).getBalance(eq(userId));
    }
    @Test
    public void testWithdrawFromNonExistentUser() throws Exception {
        Long userId = 99L;

        doThrow(new IllegalArgumentException("User not found")).when(walletService).withdraw(eq(userId), any(Money.class));

        mockMvc.perform(post("/api/wallets/99/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).withdraw(eq(userId), any(Money.class));
    }


}
