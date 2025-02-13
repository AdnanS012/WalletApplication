package com.example.WalletApp;


import Domain.User;
import Money.Money;
import UserRepository.IUserRepository;
import Service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUserSuccessfully() {
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        userService.registerUser("testUser", "securePassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testDuplicateUserRegistrationThrowsException() {
        when(userRepository.existsByUsername("duplicateUser")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser("duplicateUser", "password123"),
                "Duplicate registration should throw exception");
    }
    @Test
    public void testDepositToNonExistentUserThrowsException() {
        // Given: User does not exist
        when(userRepository.findById("unknownUser")).thenReturn(Optional.empty());

        // Then: Trying to deposit should throw an exception
        assertThrows(IllegalArgumentException.class, () ->
                userService.deposit("unknownUser", new Money(new BigDecimal("50.00")))
        );

        // Verify that save() was never called
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void testWithdrawFromWalletSuccessfully() {
        // Given: A user with username "testUser" who has INR 100.00 in the wallet
        User user = new User("testUser", "securePassword");
        user.depositToWallet(new Money(new BigDecimal("100.00")));
        when(userRepository.findById("testUser")).thenReturn(java.util.Optional.of(user));

        // When: Withdraw INR 50.00
        Money withdrawalAmount = new Money(new BigDecimal("50.00"));
        userService.withdraw("testUser", withdrawalAmount);

        // Then: The remaining balance should be INR 50.00
        assertTrue(user.canWithdrawFromWallet(new Money(new BigDecimal("50.00"))),
                "User should be able to withdraw the remaining balance");

        verify(userRepository, times(1)).save(user);  // Ensure the user is saved after withdrawal
    }

    @Test
    public void testWithdrawFromWalletWithInsufficientBalanceThrowsException() {
        // Given: A user with username "testUser" who has INR 50.00 in the wallet
        User user = new User("testUser", "securePassword");
        user.depositToWallet(new Money(new BigDecimal("50.00")));
        when(userRepository.findById("testUser")).thenReturn(java.util.Optional.of(user));

        // When: Attempt to withdraw INR 100.00 (more than the balance)
        Money withdrawalAmount = new Money(new BigDecimal("100.00"));

        // Then: An exception should be thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.withdraw("testUser", withdrawalAmount);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Insufficient balance"),
                "Exception message should contain 'Insufficient balance'");
    }

    @Test
    public void testWithdrawFromNonExistentUserThrowsException() {
        // Given: User does not exist
        when(userRepository.findById("unknownUser")).thenReturn(Optional.empty());

        // Then: Trying to withdraw should throw an exception
        assertThrows(IllegalArgumentException.class, () ->
                userService.withdraw("unknownUser", new Money(new BigDecimal("50.00")))
        );

        // Verify that save() was never called
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void testAuthenticateUserSuccessfully() {
        // Given: A user exists with the correct password
        User user = new User("testUser", "securePassword");
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        // When: The user is authenticated with the correct password
        boolean isAuthenticated = userService.authenticateUser("testUser", "securePassword");

        // Then: The authentication should be successful
        assertTrue(isAuthenticated);
    }

    @Test
    public void testAuthenticateUserWithWrongPassword() {
        // Given: A user exists with a different password
        User user = new User("testUser", "securePassword");
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        // When: The user is authenticated with the wrong password
        boolean isAuthenticated = userService.authenticateUser("testUser", "wrongPassword");

        // Then: The authentication should fail
        assertFalse(isAuthenticated);
    }

    @Test
    public void testAuthenticateNonExistentUser() {
        // Given: No user exists with the given username
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

        // When: The user is authenticated
        boolean isAuthenticated = userService.authenticateUser("nonExistentUser", "anyPassword");

        // Then: The authentication should fail
        assertFalse(isAuthenticated);
    }

    @Test
    public void testRegisterUserWithEmptyUsernameThrowsException() {
        // Trying to register a user with empty username should throw an exception
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("", "password123")
        );
    }

    @Test
    public void testRegisterUserWithEmptyPasswordThrowsException() {
        // Trying to register a user with empty password should throw an exception
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("username", "")
        );

}}
