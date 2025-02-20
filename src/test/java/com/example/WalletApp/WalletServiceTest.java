package com.example.WalletApp;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Domain.Transaction;
import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Domain.Wallet;
import com.example.WalletApp.Repository.IUserRepository;
import com.example.WalletApp.Repository.TransactionRepository;
import com.example.WalletApp.Service.CurrencyConversionService;
import com.example.WalletApp.Service.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;


import java.math.BigDecimal;
import java.util.Currency;
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
    private TransactionRepository transactionRepository;
    @Mock
    private CurrencyConversionService currencyConversionService;

    private User sender;
    private User receiver;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sender = new User("sender", "password");
        receiver = new User("receiver", "password");

    }

    @Test
    public void testDepositSuccess() {
        // Arrange
        Long userId = 1L;
        Money depositAmount = new Money(new BigDecimal("100.00"),Currency.getInstance("INR"));
        User mockUser = new User("testUser", "securePassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        walletService.deposit(userId, depositAmount);

        // Assert
        assertEquals(depositAmount, mockUser.getBalanceForResponse());
        verify(userRepository, times(1)).save(mockUser);
    }
    @Test
    public void testDepositUserNotFound() {
        // Arrange
        Long userId = 99L;
        Money depositAmount = new Money(new BigDecimal("100.00"),Currency.getInstance("INR"));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> walletService.deposit(userId, depositAmount));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDepositToNonExistentUserThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                walletService.deposit(1L, new Money(new BigDecimal("50.00"),Currency.getInstance("INR")))
        );
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testWithdrawSuccess() {
        Long userId = 1L;
        Money initialBalance = new Money(new BigDecimal("200.00"), Currency.getInstance("INR"));
        Money withdrawalAmount = new Money(new BigDecimal("50.00"), Currency.getInstance("INR"));

        sender.depositToWallet(initialBalance);
        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));

        walletService.withdraw(userId, withdrawalAmount);

        assertEquals(new Money(new BigDecimal("150.00"), Currency.getInstance("INR")),
                sender.getBalanceForResponse(), "Remaining balance should be correct after withdrawal");

        verify(userRepository, times(1)).save(sender);
    }

    @Test
    public void testWithdrawFromWalletSuccessfully() {
        User user = new User("testUser", "securePassword");
        user.depositToWallet(new Money(new BigDecimal("100.00"),Currency.getInstance("INR")));
        when(userRepository.findById((1L))).thenReturn(Optional.of(user));

        Money withdrawalAmount = new Money(new BigDecimal("50.00"),Currency.getInstance("INR"));
        walletService.withdraw(1L, withdrawalAmount);

        assertTrue(user.canWithdrawFromWallet(new Money(new BigDecimal("50.00"),Currency.getInstance("INR"))),
                "User should be able to withdraw the remaining balance");

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testWithdrawFromWalletWithInsufficientBalanceThrowsException() {
        User user = new User("testUser", "securePassword");
        user.depositToWallet(new Money(new BigDecimal("50.00"),Currency.getInstance("INR")));
        when(userRepository.findById((1L))).thenReturn(Optional.of(user));

        Money withdrawalAmount = new Money(new BigDecimal("100.00"),Currency.getInstance("INR"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> walletService.withdraw(1L, withdrawalAmount));

        assertTrue(exception.getMessage().contains("Insufficient balance"),
                "Exception message should contain 'Insufficient balance'");
    }


    @Test
    public void testWithdrawFromNonExistentUserThrowsException() {
        when(userRepository.findById((1L))).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                walletService.withdraw(1L, new Money(new BigDecimal("50.00"),Currency.getInstance("INR")))
        );
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void testWithdrawInsufficientBalance() {
        // Arrange
        Long userId = 1L;
        Money initialBalance = new Money(new BigDecimal("50.00"), Currency.getInstance("INR"));
        Money withdrawAmount = new Money(new BigDecimal("100.00"),Currency.getInstance("INR"));
        User mockUser = new User("testUser", "securePassword");
        mockUser.depositToWallet(initialBalance);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> walletService.withdraw(userId, withdrawAmount));

        assertEquals("Insufficient balance", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void testGetBalanceSuccess() {
        // Arrange
        Long userId = 1L;
        Money expectedBalance = new Money(new BigDecimal("200.00"),Currency.getInstance("INR"));
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
    public void testGetBalanceUserNotFound() {
        // Arrange
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> walletService.getBalance(userId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testTransferMoneyFromINRToUSD() {
        sender = new User("sender", "password");
        sender.depositToWallet(new Money(new BigDecimal("500.00"), Currency.getInstance("INR")));
        receiver = new User("receiver", "password");
        Money zeroUsd = new Money(BigDecimal.ZERO, Currency.getInstance("USD"));
        ReflectionTestUtils.setField(receiver, "wallet", new Wallet(zeroUsd));

        //  Manually set user IDs to avoid null issues
        ReflectionTestUtils.setField(sender, "id", 1L);
        ReflectionTestUtils.setField(receiver, "id", 2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        Money convertedAmount = new Money(new BigDecimal("6.00"), Currency.getInstance("USD"));
        when(currencyConversionService.convert(any(), anyString(), anyString())).thenReturn(convertedAmount);

        walletService.transferMoney(1L, 2L, new Money(new BigDecimal("500.00"), Currency.getInstance("INR")));

        verify(transactionRepository, times(2)).save(any(Transaction.class));

        assertEquals(new Money(BigDecimal.ZERO, Currency.getInstance("INR")), sender.getBalanceForResponse());
        assertEquals(convertedAmount, receiver.getBalanceForResponse());
    }


    @Test
    public void testTransferMoneySameCurrency() {
        sender = new User("sender", "password");
        sender.depositToWallet(new Money(new BigDecimal("500.00"), Currency.getInstance("INR")));
        receiver = new User("receiver", "password");
        Money zeroInr = new Money(BigDecimal.ZERO, Currency.getInstance("INR"));
        ReflectionTestUtils.setField(receiver, "wallet", new Wallet(zeroInr));
        // Set user IDs
        ReflectionTestUtils.setField(sender, "id", 1L);
        ReflectionTestUtils.setField(receiver, "id", 2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        // Act: Transfer money within the same currency
        walletService.transferMoney(1L, 2L, new Money(new BigDecimal("200.00"), Currency.getInstance("INR")));

        // Verify that transaction records are saved
        verify(transactionRepository, times(2)).save(any(Transaction.class));

        // Assertions:
        assertEquals(new Money(new BigDecimal("300.00"), Currency.getInstance("INR")), sender.getBalanceForResponse());
        assertEquals(new Money(new BigDecimal("200.00"), Currency.getInstance("INR")), receiver.getBalanceForResponse());
    }

    @Test
    public void testTransferFailsDueToInsufficientBalance() {
        sender = new User("sender", "password");
        sender.depositToWallet(new Money(new BigDecimal("100.00"), Currency.getInstance("INR")));
        receiver = new User("receiver", "password");
        Money zeroInr = new Money(BigDecimal.ZERO, Currency.getInstance("INR"));
        ReflectionTestUtils.setField(receiver, "wallet", new Wallet(zeroInr));

        ReflectionTestUtils.setField(sender, "id", 1L);
        ReflectionTestUtils.setField(receiver, "id", 2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->  walletService.transferMoney(1L, 2L, new Money(new BigDecimal("200.00"), Currency.getInstance("INR"))));

        assertEquals("🚨 Insufficient balance!", exception.getMessage());

        // Verify no transactions were recorded
        verify(transactionRepository, times(0)).save(any(Transaction.class));

        // Verify balances remain unchanged
        assertEquals(new Money(new BigDecimal("100.00"), Currency.getInstance("INR")), sender.getBalanceForResponse());
        assertEquals(new Money(BigDecimal.ZERO, Currency.getInstance("INR")), receiver.getBalanceForResponse());
    }

    @Test
    public void testTransferFailsWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> walletService.transferMoney(1L, 2L, new Money(new BigDecimal("100.00"), Currency.getInstance("INR"))));

        assertEquals("🚨 Sender not found!", exception.getMessage());

        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    public void testTransferMoneyDifferentCurrency_USD_to_INR() {
        sender = new User("sender", "password");

        //  Ensure sender's wallet starts in USD
        Money initialUsd = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));
        ReflectionTestUtils.setField(sender, "wallet", new Wallet(initialUsd));

        receiver = new User("receiver", "password");

        //  Ensure receiver's wallet starts in INR
        Money zeroInr = new Money(BigDecimal.ZERO, Currency.getInstance("INR"));
        ReflectionTestUtils.setField(receiver, "wallet", new Wallet(zeroInr));

        //  Manually set user IDs
        ReflectionTestUtils.setField(sender, "id", 1L);
        ReflectionTestUtils.setField(receiver, "id", 2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        // Mock currency conversion (10 USD -> 850 INR)
        when(currencyConversionService.convert(
                argThat(money -> money.getCurrency().equals(Currency.getInstance("USD"))),
                eq("USD"),
                eq("INR")
        )).thenReturn(new Money(new BigDecimal("850.00"), Currency.getInstance("INR")));

        // Execute Transfer
        walletService.transferMoney(1L, 2L, new Money(new BigDecimal("10.00"), Currency.getInstance("USD")));

        // Verify Transactions Saved
        verify(transactionRepository, times(2)).save(any(Transaction.class));

        // Assert Sender Balance
        assertEquals(new Money(BigDecimal.ZERO, Currency.getInstance("USD")), sender.getBalanceForResponse());

        // Assert Receiver Balance (should be ₹830.00)
        assertEquals(new Money(new BigDecimal("850.00"), Currency.getInstance("INR")), receiver.getBalanceForResponse());
    }
    @Test
    public void testTransferWithZeroAmount() {
        sender = new User("sender", "password");
        sender.depositToWallet(new Money(new BigDecimal("500.00"), Currency.getInstance("INR")));
        receiver = new User("receiver", "password");
        receiver.depositToWallet(new Money(BigDecimal.ZERO, Currency.getInstance("INR")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        walletService.transferMoney(1L, 2L, new Money(BigDecimal.ZERO, Currency.getInstance("INR")));

        assertEquals(new Money(new BigDecimal("500.00"), Currency.getInstance("INR")), sender.getBalanceForResponse());
        assertEquals(new Money(BigDecimal.ZERO, Currency.getInstance("INR")), receiver.getBalanceForResponse());
    }
    @Test
    public void testTransferBetweenSameUser() {
        sender = new User("sender", "password");
        sender.depositToWallet(new Money(new BigDecimal("500.00"), Currency.getInstance("INR")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));

        walletService.transferMoney(1L, 1L, new Money(new BigDecimal("100.00"), Currency.getInstance("INR")));

        assertEquals(new Money(new BigDecimal("500.00"), Currency.getInstance("INR")), sender.getBalanceForResponse());
    }
    @Test
    public void testTransferMoney_SameCurrency_EUR_to_EUR() {
        sender = new User("sender", "password");
        Money initialEur = new Money(new BigDecimal("200.00"), Currency.getInstance("EUR"));
        ReflectionTestUtils.setField(sender, "wallet", new Wallet(initialEur));

        receiver = new User("receiver", "password");
        Money zeroEur = new Money(BigDecimal.ZERO, Currency.getInstance("EUR"));
        ReflectionTestUtils.setField(receiver, "wallet", new Wallet(zeroEur));

        ReflectionTestUtils.setField(sender, "id", 1L);
        ReflectionTestUtils.setField(receiver, "id", 2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        Money transferAmount = new Money(new BigDecimal("75.00"), Currency.getInstance("EUR"));

        walletService.transferMoney(1L, 2L, transferAmount);

        verify(transactionRepository, times(2)).save(any(Transaction.class));

        // Check sender balance
        assertEquals(new Money(new BigDecimal("125.00"), Currency.getInstance("EUR")), sender.getBalanceForResponse());

        // Check receiver balance
        assertEquals(new Money(new BigDecimal("75.00"), Currency.getInstance("EUR")), receiver.getBalanceForResponse());
    }
    @Test
    public void testAtomicity_WalletServiceRollbackOnFailure() {
        Long senderId = 1L;
        Long receiverId = 2L;
        Money transferAmount = new Money(new BigDecimal("500.00"), Currency.getInstance("USD"));
        Money convertedAmount = new Money(new BigDecimal("41500.00"), Currency.getInstance("INR"));

        User sender = new User("sender", "password");
        User receiver = new User("receiver", "password");

        ReflectionTestUtils.setField(sender, "wallet", new Wallet(transferAmount));
        ReflectionTestUtils.setField(sender, "id", senderId);
        ReflectionTestUtils.setField(receiver, "id", receiverId);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));

        when(currencyConversionService.convert(any(), eq("USD"), eq("INR"))).thenReturn(convertedAmount);

        doThrow(new RuntimeException("Simulated failure after sender withdrawal"))
                .when(transactionRepository).save(any(Transaction.class));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            walletService.transferMoney(senderId, receiverId, transferAmount);
        });

        assertEquals("Simulated failure after sender withdrawal", exception.getMessage());

        verify(transactionRepository, never()).save(any(Transaction.class));

        // Ensure rollback (sender still has full balance)
        assertEquals(transferAmount, sender.getBalanceForResponse());

        // Ensure receiver’s balance is still zero
        assertEquals(new Money(BigDecimal.ZERO, Currency.getInstance("INR")), receiver.getBalanceForResponse());
    }


}
