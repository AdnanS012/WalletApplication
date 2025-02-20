package com.example.WalletApp;


import com.example.WalletApp.Controller.WalletController;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerTest {
    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;  // Mock WalletService

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
    public void testGetBalance_InvalidUserIdFormat() throws Exception {
        mockMvc.perform(get("/users/invalid/wallet")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
