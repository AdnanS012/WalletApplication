package com.example.WalletApp;

import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Domain.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    public void testUserCreation() {
        User user = new User("JohnDoe", "securePassword");
        assertEquals("JohnDoe", user.getUsername());
        assertNotNull(user.getBalanceForResponse());
        assertEquals(new Money(BigDecimal.ZERO, Currency.getInstance("INR")), user.getBalanceForResponse());
    }

    @Test
    public void testAuthenticateSuccess() {
        User user = new User("user5", "securePassword");
        assertTrue(user.authenticate("securePassword"),
                "Authentication should succeed when the correct password is provided");
    }

    @Test
    public void testAuthenticateFailure() {
        User user = new User("user5", "securePassword");
        assertTrue(!user.authenticate("wrongPassword"),
                "Authentication should fail when the wrong password is provided");
    }

    @Test
    public void testNewUserCannotWithdrawFunds() {
        User user = new User("user1", "password1");
        // A new user's wallet has no funds.
        assertFalse(user.canWithdrawFromWallet(new Money(new BigDecimal("0.01"), Currency.getInstance("INR"))), "New user should not be able to withdraw any funds");
    }

    @Test
    public void testDepositToWalletEnablesWithdrawal() {
        User user = new User("user2", "password2");
        user.depositToWallet(new Money(new BigDecimal("100.00"),Currency.getInstance("INR")));
        assertTrue(user.canWithdrawFromWallet(new Money(new BigDecimal("100.00"),Currency.getInstance("INR"))), "User should be able to withdraw deposited amount");
        assertFalse(user.canWithdrawFromWallet(new Money(new BigDecimal("100.01"),Currency.getInstance("INR"))), "User should not be able to withdraw more than deposited amount");
    }

    @Test
    public void testWithdrawFromWalletReducesAvailableFunds() {
        User user = new User("user3", "password3");
        user.depositToWallet(new Money(new BigDecimal("150.00"),Currency.getInstance("INR")));
        user.withdrawFromWallet(new Money(new BigDecimal("50.00"),Currency.getInstance("INR")));
        assertTrue(user.canWithdrawFromWallet(new Money(new BigDecimal("100.00"),Currency.getInstance("INR"))), "User should be able to withdraw remaining funds");
        assertFalse(user.canWithdrawFromWallet(new Money(new BigDecimal("100.01"),Currency.getInstance("INR"))), "User should not be able to withdraw more than remaining funds");
    }
    @Test
    public void testWithdrawFromWallet_InsufficientFunds() {
        User user = new User("testUser", "password");
        user.depositToWallet(new Money(new BigDecimal("100.00"), Currency.getInstance("INR")));

        assertThrows(IllegalArgumentException.class, () ->
                        user.withdrawFromWallet(new Money(new BigDecimal("200.00"), Currency.getInstance("INR"))),
                "Should not allow withdrawal greater than balance");
    }


}