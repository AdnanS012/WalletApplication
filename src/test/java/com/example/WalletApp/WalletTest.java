package com.example.WalletApp;

import Domain.Wallet;
import Domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {


    private Wallet wallet;

    @BeforeEach
    public void setUp() {
        wallet = new Wallet();
    }

    @Test
    public void testWalletInitialBalanceIsZero() {
        assertTrue(wallet.canWithdraw(Money.Zero));
    }

    @Test
    public void testDepositIncreasesBalance() {
        wallet.deposit(new Money(BigDecimal.valueOf(100)));
        assertTrue(wallet.canWithdraw(new Money(BigDecimal.valueOf(100))));
    }

    @Test
    public void testWithdrawDecreasesBalance() {
        wallet.deposit(new Money(BigDecimal.valueOf(200)));
        wallet.withdraw(new Money(BigDecimal.valueOf(50)));

        assertTrue(wallet.canWithdraw(new Money(BigDecimal.valueOf(150))));
        assertFalse(wallet.canWithdraw(new Money(BigDecimal.valueOf(200))));
    }

    @Test
    public void testWithdrawMoreThanBalanceThrowsException() {
        wallet.deposit(new Money(BigDecimal.valueOf(50)));
        assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(new Money(BigDecimal.valueOf(100))));
    }

    @Test
    public void testDepositNegativeAmountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(new Money(BigDecimal.valueOf(-10))));
    }
    @Test
    public void testDepositAndWithdrawLargeAmounts() {
        wallet.deposit(new Money(BigDecimal.valueOf(1_000_000)));
        wallet.withdraw(new Money(BigDecimal.valueOf(500_000)));

        assertTrue(wallet.canWithdraw(new Money(BigDecimal.valueOf(500_000))));
        assertFalse(wallet.canWithdraw(new Money(BigDecimal.valueOf(500_001))));
    }
    @Test
    public void testMultipleDepositsAndWithdrawals() {
        wallet.deposit(new Money(BigDecimal.valueOf(100)));
        wallet.deposit(new Money(BigDecimal.valueOf(50)));
        wallet.withdraw(new Money(BigDecimal.valueOf(30)));
        wallet.withdraw(new Money(BigDecimal.valueOf(20)));

        assertTrue(wallet.canWithdraw(new Money(BigDecimal.valueOf(100))));
        assertFalse(wallet.canWithdraw(new Money(BigDecimal.valueOf(101))));
    }

}

