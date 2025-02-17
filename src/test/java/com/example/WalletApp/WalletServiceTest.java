package com.example.WalletApp;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Repository.IUserRepository;
import com.example.WalletApp.Service.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class WalletServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeposit_Success() {
        // Arrange
        Long userId = 1L;
        Money depositAmount = new Money(new BigDecimal("100.00"));
        User mockUser = new User("testUser", "securePassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        walletService.deposit(userId, depositAmount);

        // Assert
        assertEquals(depositAmount, mockUser.getBalanceForResponse());
        verify(userRepository, times(1)).save(mockUser);
    }
    @Test
    public void testDeposit_UserNotFound() {
        // Arrange
        Long userId = 99L;
        Money depositAmount = new Money(new BigDecimal("100.00"));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.deposit(userId, depositAmount);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDepositToNonExistentUserThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                walletService.deposit(1L, new Money(new BigDecimal("50.00")))
        );
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void testWithdrawFromWalletSuccessfully() {
        User user = new User("testUser", "securePassword");
        user.depositToWallet(new Money(new BigDecimal("100.00")));
        when(userRepository.findById((1L))).thenReturn(Optional.of(user));

        Money withdrawalAmount = new Money(new BigDecimal("50.00"));
        walletService.withdraw(1L, withdrawalAmount);

        assertTrue(user.canWithdrawFromWallet(new Money(new BigDecimal("50.00"))),
                "User should be able to withdraw the remaining balance");

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testWithdrawFromWalletWithInsufficientBalanceThrowsException() {
        User user = new User("testUser", "securePassword");
        user.depositToWallet(new Money(new BigDecimal("50.00")));
        when(userRepository.findById((1L))).thenReturn(Optional.of(user));

        Money withdrawalAmount = new Money(new BigDecimal("100.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.withdraw(1L, withdrawalAmount);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance"),
                "Exception message should contain 'Insufficient balance'");
    }


    @Test
    public void testWithdrawFromNonExistentUserThrowsException() {
        when(userRepository.findById((1L))).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                walletService.withdraw(1L, new Money(new BigDecimal("50.00")))
        );
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void testWithdraw_InsufficientBalance() {
        // Arrange
        Long userId = 1L;
        Money initialBalance = new Money(new BigDecimal("50.00"));
        Money withdrawAmount = new Money(new BigDecimal("100.00"));
        User mockUser = new User("testUser", "securePassword");
        mockUser.depositToWallet(initialBalance);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.withdraw(userId, withdrawAmount);
        });

        assertEquals("Insufficient balance", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void testGetBalance_Success() {
        // Arrange
        Long userId = 1L;
        Money expectedBalance = new Money(new BigDecimal("200.00"));
        User mockUser = new User("testUser", "securePassword");
        mockUser.depositToWallet(expectedBalance);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        Money balance = walletService.getBalance(userId);

        // Assert
        assertEquals(expectedBalance, balance);
        verify(userRepository, times(1)).findById(userId);
    }
    @Test
    public void testGetBalance_UserNotFound() {
        // Arrange
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.getBalance(userId);
        });

        assertEquals("User not found", exception.getMessage());
    }
}
