package com.example.WalletApp;

import Controller.UserController;
import Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.*;
import Money.Money;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

        mockMvc.perform(post("/api/users/register")
                        .param("username", "testUser")
                        .param("password", "securePassword"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));

        verify(userService, times(1)).registerUser("testUser", "securePassword");
    }

    @Test
    public void testDepositToWallet() throws Exception {
        doNothing().when(userService).deposit(eq("testUser"), any(Money.class));

        mockMvc.perform(post("/api/users/testUser/deposit")
                        .contentType(MediaType.APPLICATION_JSON)  //  Set request content type
                        .content("{\"amount\": 100.00}")  //  Send JSON instead of .param()
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  //  Expect HTTP 200 OK
                .andExpect(content().string("Deposit successful!"));

        verify(userService, times(1)).deposit(eq("testUser"), any(Money.class));
    }

    @Test
    public void testWithdrawFromWallet() throws Exception {
        doNothing().when(userService).withdraw(eq("testUser"), any(Money.class));

        mockMvc.perform(post("/api/users/testUser/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)  // ✅ Set request content type
                        .content("{\"amount\": 50.00}")  // ✅ Send JSON in request body
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // ✅ Expect HTTP 200 OK
                .andExpect(content().string("Withdrawal successful!"));

        verify(userService, times(1)).withdraw(eq("testUser"), any(Money.class));
    }

}
