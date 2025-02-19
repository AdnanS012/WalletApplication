package com.example.WalletApp;


import com.example.WalletApp.Controller.WalletController;
import com.example.WalletApp.DTO.TransactionResponse;
import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Enum.TransactionType;
import com.example.WalletApp.Service.CurrencyConversionService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

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
    @Mock
    private CurrencyConversionService currencyConversionService;

    @InjectMocks
    private WalletController walletController;
    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

    }


    @Test
    public void testDeposit() throws Exception {
        Long userId = 1L;
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("INR"));

        doNothing().when(walletService).deposit(eq(userId), any(Money.class));

        mockMvc.perform(post("/users/1/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit successful!"));

        verify(walletService, times(1)).deposit(eq(userId), any(Money.class));
    }

    @Test
    public void testWithdraw() throws Exception {
        Long userId = 1L;
        Money amount = new Money(new BigDecimal("50.00"), Currency.getInstance("INR"));

        doNothing().when(walletService).withdraw(eq(userId), any(Money.class));

        mockMvc.perform(post("/users/1/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdrawal successful!"));

        verify(walletService, times(1)).withdraw(eq(userId), any(Money.class));
    }

    @Test
    public void testGetBalance() throws Exception {
        Long userId = 1L;
        Money balance = new Money(new BigDecimal("200.00"), Currency.getInstance("INR"));

        when(walletService.getBalance(eq(userId))).thenReturn(balance);

        mockMvc.perform(get("/users/1/wallet")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(balance)));

        verify(walletService, times(1)).getBalance(eq(userId));
    }

    @Test
    public void testTransferMoney() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("50.00"),Currency.getInstance("INR"));

        doNothing().when(walletService).transferMoney(eq(senderId), eq(receiverId), any(Money.class));

        mockMvc.perform(post("/users/1/wallet/transfer/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful!"));

        verify(walletService, times(1)).transferMoney(eq(senderId), eq(receiverId), any(Money.class));
    }

    @Test
    public void testGetTransactions() throws Exception {
        Long userId = 1L;
        List<TransactionResponse> transactions = Arrays.asList(
                new TransactionResponse(1L, 1L, new Money(new BigDecimal("100.00"), Currency.getInstance("INR")), TransactionType.DEPOSIT, LocalDateTime.now()),
                new TransactionResponse(2L, 1L, new Money(new BigDecimal("50.00"),Currency.getInstance("INR")), TransactionType.WITHDRAWAL, LocalDateTime.now())
        );

        when(walletService.getTransactions(eq(userId))).thenReturn(transactions);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc.perform(get("/users/1/wallet/transactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(transactions)));

        verify(walletService, times(1)).getTransactions(eq(userId));
    }
    @Test
    public void testWithdrawFromNonExistentUser() throws Exception {
        Long userId = 99L;
        Money amount = new Money(new BigDecimal("50.00"),Currency.getInstance("INR"));

        doThrow(new IllegalArgumentException("User not found")).when(walletService).withdraw(eq(userId), any(Money.class));

        mockMvc.perform(post("/users/99/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).withdraw(eq(userId), any(Money.class));
    }

    @Test
    public void testTransferMoney_InsufficientBalance() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("1000.00"),Currency.getInstance("INR"));

        doThrow(new IllegalArgumentException("Insufficient balance"))
                .when(walletService).transferMoney(eq(senderId), eq(receiverId), any(Money.class));

        mockMvc.perform(post("/users/1/wallet/transfer/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));

        verify(walletService, times(1)).transferMoney(eq(senderId), eq(receiverId), any(Money.class));
    }




}
