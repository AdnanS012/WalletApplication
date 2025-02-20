package com.example.WalletApp;

import com.example.WalletApp.Domain.Wallet;
import com.example.WalletApp.Domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {


    private Wallet wallet;
    private Currency inr;
    private  Currency usd;

    @BeforeEach
    public void setUp() {
        inr = Currency.getInstance("INR");
        usd = Currency.getInstance("USD");

        wallet = Wallet.createDefaultWallet();
    }

    @Test
    public void testWalletInitialBalanceIsZero() {
        assertEquals(new Money(BigDecimal.ZERO, inr), wallet.provideBalance(),
                "A new wallet should have a balance of zero");
    }

        @Test
    public void testDepositIncreasesBalance() {
        wallet.deposit(new Money(BigDecimal.valueOf(100),inr));
        assertTrue(wallet.canWithdraw(new Money(BigDecimal.valueOf(100),inr)));
    }

    @Test
    public void testWithdrawDecreasesBalance() {
        wallet.deposit(new Money(BigDecimal.valueOf(200),inr));
        wallet.withdraw(new Money(BigDecimal.valueOf(50),inr));

        assertTrue(wallet.canWithdraw(new Money(BigDecimal.valueOf(150),inr)));
        assertFalse(wallet.canWithdraw(new Money(BigDecimal.valueOf(200),inr)));
    }

    @Test
    public void testWithdrawMoreThanBalanceThrowsException() {
        wallet.deposit(new Money(BigDecimal.valueOf(50),inr));
        assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(new Money(BigDecimal.valueOf(100),inr)));
    }

    @Test
    public void testDepositNegativeAmountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(new Money(BigDecimal.valueOf(-10),inr)));
    }
    @Test
    public void testDepositAndWithdrawLargeAmounts() {
        wallet.deposit(new Money(BigDecimal.valueOf(1_000_000),inr));
        wallet.withdraw(new Money(BigDecimal.valueOf(500_000),inr));

        assertTrue(wallet.canWithdraw(new Money(BigDecimal.valueOf(500_000),inr)));
        assertFalse(wallet.canWithdraw(new Money(BigDecimal.valueOf(500_001),inr)));
    }
    @Test
    public void testMultipleDepositsAndWithdrawals() {
        wallet.deposit(new Money(BigDecimal.valueOf(100),inr));
        wallet.deposit(new Money(BigDecimal.valueOf(50),inr));
        wallet.withdraw(new Money(BigDecimal.valueOf(30),inr));
        wallet.withdraw(new Money(BigDecimal.valueOf(20),inr));

        assertTrue(wallet.canWithdraw(new Money(BigDecimal.valueOf(100),inr)));
        assertFalse(wallet.canWithdraw(new Money(BigDecimal.valueOf(101),inr)));
    }
    @Test
    public void testCurrencyMismatchOnDeposit() {
        Money usdMoney = new Money(new BigDecimal("100.00"), usd);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                        wallet.deposit(usdMoney),
                "Should not allow deposits in different currencies");

        assertEquals("Currency mismatch in wallet", exception.getMessage());
    }

    @Test
    public void testCurrencyMismatchOnWithdraw() {
        Money inrMoney = new Money(new BigDecimal("100.00"), inr);
        wallet.deposit(inrMoney);

        Money usdMoney = new Money(new BigDecimal("50.00"), usd);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                        wallet.withdraw(usdMoney),
                "Should not allow withdrawals in different currencies");

        assertEquals("Currency mismatch in wallet", exception.getMessage());
    }
    @Test
    public void testWalletInitializationWithDifferentCurrency() {
        Money usdMoney = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
        Wallet wallet = new Wallet(usdMoney);

        assertEquals(new BigDecimal("100.00"), wallet.provideBalance().getAmount());
        assertEquals(Currency.getInstance("USD"), wallet.getCurrency());
    }
    @Test
    public void testDepositWithDifferentCurrencyShouldFail() {
        Wallet wallet = new Wallet(new Money(new BigDecimal("100.00"), Currency.getInstance("USD")));
        Money depositAmount = new Money(new BigDecimal("50.00"), Currency.getInstance("INR"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            wallet.deposit(depositAmount);
        });

        assertEquals("Currency mismatch in wallet", exception.getMessage());
    }
    @Test
    public void testWalletInitializationWithDefaultCurrency() {
        Wallet wallet = Wallet.createDefaultWallet(); // Default constructor
        assertEquals(Money.Zero, wallet.provideBalance());
        assertEquals(Currency.getInstance("INR"), wallet.getCurrency()); // Default currency should be INR
    }
}

