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

//@WebMvcTest(UserController.class)
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
    public void testDepositToWallet() throws Exception {
        doNothing().when(userService).deposit(eq(1L), any(Money.class));

        mockMvc.perform(post("/api/users/1/wallets/deposit")
                        .contentType(MediaType.APPLICATION_JSON)  //  Set request content type
                        .content("{\"amount\": 100.00}")  //  Send JSON instead of .param()
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  //  Expect HTTP 200 OK
                .andExpect(content().string("Deposit successful!"));

        verify(userService, times(1)).deposit( eq(1L), any(Money.class));
    }

    @Test
    public void testWithdrawFromWallet() throws Exception {
        doNothing().when(userService).withdraw(eq(1L), any(Money.class));

        mockMvc.perform(post("/api/users/1/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdrawal successful!"));

        verify(userService, times(1)).withdraw(eq(1L), any(Money.class));
    }
    @Test
    public void testWithdrawFailure() throws Exception {
        doThrow(new RuntimeException("Withdrawal failed")).when(userService).withdraw(eq(1L), any(Money.class));

        mockMvc.perform(post("/api/users/1/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal Server Error"));

        verify(userService, times(1)).withdraw(eq(1L), any(Money.class));
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
}
