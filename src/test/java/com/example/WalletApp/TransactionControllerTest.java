package com.example.WalletApp;

import com.example.WalletApp.Controller.TransactionController;
import com.example.WalletApp.DTO.TransactionResponse;
import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Domain.Transaction;
import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Enum.TransactionType;
import com.example.WalletApp.Repository.IUserRepository;
import com.example.WalletApp.Repository.TransactionRepository;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerTest {
    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;  // Mock UserService
    @Mock
    private CurrencyConversionService currencyConversionService;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private IUserRepository userRepository;
    @InjectMocks
    private TransactionController transactionController;
    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

    }

    @Test
    public void testTransferMoneyMoney() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("50.00"), Currency.getInstance("INR"));

        doNothing().when(walletService).transferMoney(eq(senderId), eq(receiverId), any(Money.class));

        mockMvc.perform(post("/users/1/transactions/2")
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

        mockMvc.perform(get("/users/1/transactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(transactions)));

        verify(walletService, times(1)).getTransactions(eq(userId));
    }

    @Test
    public void testTransferMoneyMoney_InsufficientBalance() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("1000.00"),Currency.getInstance("INR"));

        doThrow(new IllegalArgumentException("Insufficient balance"))
                .when(walletService).transferMoney(eq(senderId), eq(receiverId), any(Money.class));

        mockMvc.perform(post("/users/1/transactions/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));

        verify(walletService, times(1)).transferMoney(eq(senderId), eq(receiverId), any(Money.class));
    }

    @Test
    public void testTransferMoneyMoneyUSDToINR() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("100.00"),Currency.getInstance("USD"));

        doNothing().when(walletService).transferMoney(eq(senderId), eq(receiverId), any(Money.class));

        mockMvc.perform(post("/users/1/transactions/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful!"));

        verify(walletService, times(1)).transferMoney(eq(senderId), eq(receiverId), any(Money.class));
    }

    @Test
    public void testTransferMoneyMoneySameCurrency() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("200.00"), Currency.getInstance("INR"));

        mockMvc.perform(post("/users/1/transactions/2")
                        .param("senderId", String.valueOf(senderId))
                        .param("receiverId", String.valueOf(receiverId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful!"));

        verify(walletService).transferMoney(senderId, receiverId, amount);

    }

    @Test
    public void testTransferMoneyMoney_DifferentCurrency_USD_to_INR() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money transferAmount = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money convertedAmount = new Money(new BigDecimal("850.00"), Currency.getInstance("INR"));

        when(currencyConversionService.convert(
                argThat(money -> money.getCurrency().equals(Currency.getInstance("USD"))),
                eq("USD"),
                eq("INR")
        )).thenReturn(convertedAmount);

        doNothing().when(walletService).transferMoney(senderId, receiverId, transferAmount);

        mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}",senderId, receiverId)
                        .param("userId", senderId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transferAmount)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Transfer successful!")));

        verify(walletService, times(1)).transferMoney(senderId, receiverId, transferAmount);
    }

    @Test
    public void testTransferMoneyMoneyEURToINR() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("75.00"), Currency.getInstance("EUR"));

        doNothing().when(walletService).transferMoney(eq(senderId), eq(receiverId), any(Money.class));

        mockMvc.perform(post("/users/1/transactions/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Transfer successful!")));

        verify(walletService, times(1)).transferMoney(eq(senderId), eq(receiverId), any(Money.class));
    }
    @Test
    public void testTransferMoneyMoney_ReceiverNotExist() throws Exception {
        Long senderId = 1L;
        Long receiverId = 999L; // Receiver does not exist
        Money amount = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));

        doThrow(new IllegalArgumentException("Receiver does not exist"))
                .when(walletService).transferMoney(senderId, receiverId, amount);

        mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Receiver does not exist"));
    }
    @Test
    public void testTransferMoneyMoney_MaximumAmount() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money maxAmount = new Money(new BigDecimal("1000000000.00"), Currency.getInstance("USD")); // 1 Billion USD

        when(currencyConversionService.convert(any(), anyString(), anyString())).thenReturn(maxAmount);
        doNothing().when(walletService).transferMoney(senderId, receiverId, maxAmount);

        mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(maxAmount)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Transfer successful!")));

        verify(walletService, times(1)).transferMoney(senderId, receiverId, maxAmount);
    }
    @Test
    public void testTransferMoneyMoney_MinimumValidAmount() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money minAmount = new Money(new BigDecimal("0.01"), Currency.getInstance("USD")); // Minimum valid amount

        when(currencyConversionService.convert(any(), anyString(), anyString())).thenReturn(minAmount);
        doNothing().when(walletService).transferMoney(senderId, receiverId, minAmount);

        mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(minAmount)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Transfer successful!")));

        verify(walletService, times(1)).transferMoney(senderId, receiverId, minAmount);
    }
    @Test
    public void testTransferMoneyMoney_HighPrecisionAmount() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money preciseAmount = new Money(new BigDecimal("0.0000001"), Currency.getInstance("USD")); // High precision

        when(currencyConversionService.convert(any(), anyString(), anyString())).thenReturn(preciseAmount);
        doNothing().when(walletService).transferMoney(senderId, receiverId, preciseAmount);

        mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(preciseAmount)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Transfer successful!")));

        verify(walletService, times(1)).transferMoney(senderId, receiverId, preciseAmount);
    }

    @Test
    public void testTransferMoneyMoney_MultipleTransfersQuickSuccession() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));

        when(currencyConversionService.convert(any(), anyString(), anyString())).thenReturn(amount);
        doNothing().when(walletService).transferMoney(senderId, receiverId, amount);

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(amount)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Transfer successful!")));
        }

        verify(walletService, times(5)).transferMoney(senderId, receiverId, amount);
    }
    @Test
    public void testTransferMoneyMoney_CrossCurrencyChain() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Long thirdPartyId = 3L;
        Money usdAmount = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money eurAmount = new Money(new BigDecimal("9.00"), Currency.getInstance("EUR"));
        Money inrAmount = new Money(new BigDecimal("800.00"), Currency.getInstance("INR"));

        when(currencyConversionService.convert(usdAmount, "USD", "EUR")).thenReturn(eurAmount);
        when(currencyConversionService.convert(eurAmount, "EUR", "INR")).thenReturn(inrAmount);

        doNothing().when(walletService).transferMoney(senderId, receiverId, usdAmount);
        doNothing().when(walletService).transferMoney(receiverId, thirdPartyId, eurAmount);

        // USD → EUR Transfer
        mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(usdAmount)))
                .andExpect(status().isOk());

        // EUR → INR Transfer
        mockMvc.perform(post("/users/{receiverId}/transactions/{thirdPartyId}", receiverId, thirdPartyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(eurAmount)))
                .andExpect(status().isOk());

        verify(walletService, times(1)).transferMoney(senderId, receiverId, usdAmount);
        verify(walletService, times(1)).transferMoney(receiverId, thirdPartyId, eurAmount);
    }
    @Test
    public void testBatchTransfers() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));

        when(currencyConversionService.convert(any(), anyString(), anyString())).thenReturn(amount);
        doNothing().when(walletService).transferMoney(anyLong(), anyLong(), any());

        for (int i = 0; i < 100; i++) { // Simulating 100 transfers
            mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(amount)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Transfer successful!")));
        }

        verify(walletService, times(100)).transferMoney(senderId, receiverId, amount);
    }
    @Test
    public void testHighFrequencyTransfers() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("5.00"), Currency.getInstance("USD"));

        when(currencyConversionService.convert(any(), anyString(), anyString())).thenReturn(amount);
        doNothing().when(walletService).transferMoney(anyLong(), anyLong(), any());

        ExecutorService executorService = Executors.newFixedThreadPool(10); // 10 parallel threads

        for (int i = 0; i < 50; i++) { // 50 concurrent transactions
            executorService.submit(() -> {
                try {
                    mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(new ObjectMapper().writeValueAsString(amount)))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(5, TimeUnit.SECONDS));

        verify(walletService, times(50)).transferMoney(senderId, receiverId, amount);
    }
    @Test
    public void testConcurrentTransfers() throws Exception {
        Long userA = 1L, userB = 2L, userC = 3L, userD = 4L;
        Money amount = new Money(new BigDecimal("15.00"), Currency.getInstance("EUR"));

        when(currencyConversionService.convert(any(), anyString(), anyString())).thenReturn(amount);
        doNothing().when(walletService).transferMoney(anyLong(), anyLong(), any());

        List<Runnable> transactions = Arrays.asList(
                () -> performTransferMoney(userA, userB, amount),
                () -> performTransferMoney(userC, userD, amount),
                () -> performTransferMoney(userA, userD, amount),
                () -> performTransferMoney(userB, userC, amount)
        );

        ExecutorService executor = Executors.newFixedThreadPool(4);
        transactions.forEach(executor::submit);

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        verify(walletService, times(4)).transferMoney(anyLong(), anyLong(), any());
    }

    private void performTransferMoney(Long senderId, Long receiverId, Money amount) {
        try {
            mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(amount)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testTransactionRollbackOnFailure() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));

        doThrow(new RuntimeException("Simulated DB failure"))
                .when(walletService).transferMoney(senderId, receiverId, amount);

        mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isInternalServerError()); // ✅ Expect 500 error due to simulated DB failure

        // ✅ Ensure transferMoney was attempted but no transactions were saved
        verify(walletService, times(1)).transferMoney(senderId, receiverId, amount);
        verify(transactionRepository, never()).save(any(Transaction.class)); // 🚀 No transaction should be recorded
    }

    @Test
    public void testAtomicity_TransferMoneyFailsMidway() throws Exception {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money amount = new Money(new BigDecimal("500.00"), Currency.getInstance("USD"));

        // Mock currency conversion
        Money convertedAmount = new Money(new BigDecimal("41500.00"), Currency.getInstance("INR"));
        when(currencyConversionService.convert(amount, "USD", "INR")).thenReturn(convertedAmount);

        // Simulate failure AFTER sender's balance is deducted (but before deposit to receiver)
        doAnswer(invocation -> {
            throw new RuntimeException("Simulated failure after sender withdrawal");
        }).when(walletService).transferMoney(senderId, receiverId, amount);

        mockMvc.perform(post("/users/{senderId}/transactions/{receiverId}", senderId, receiverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(amount)))
                .andExpect(status().isInternalServerError()); //  Expect failure

        //  Verify no transactions were saved (ensuring rollback)
        verify(walletService, times(1)).transferMoney(senderId, receiverId, amount);
        verify(transactionRepository, never()).save(any(Transaction.class));

        //  Ensure sender's balance did not change
        verify(userRepository, never()).save(any(User.class));
    }

}
