package com.example.WalletApp;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.CurrencyConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyConversionServiceTest {

//    private CurrencyConversionService currencyConversionService;
//
//    @BeforeEach
//    public void setUp() {
//        currencyConversionService = new CurrencyConversionService();
//    }
//
//    @Test
//    public void testConvertSameCurrency() {
//        Money amount = new Money(BigDecimal.valueOf(100),Currency.getInstance("USD"));
//        Money convertedAmount = currencyConversionService.convert(amount, "USD", "USD");
//        assertEquals(amount, convertedAmount,"Conversion should return the same amount for identical currencies");
//    }
//    @Test
//    public void testConvertUsdToInr() {
//        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("USD"));
//
//        Money convertedAmount = currencyConversionService.convert(amount, "USD", "INR");
//
//        BigDecimal expectedAmount = new BigDecimal("8500.00"); // Assuming 1 USD = 85 INR
//        assertEquals(expectedAmount, convertedAmount.getAmount(), "Converted amount should match expected exchange rate");
//        assertEquals(Currency.getInstance("INR"), convertedAmount.getCurrency(), "Currency should change to INR");
//    }
//
//    @Test
//    public void testConvertInrToUsd() {
//        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("INR"));
//
//        Money convertedAmount = currencyConversionService.convert(amount, "INR", "USD");
//
//        BigDecimal expectedAmount = new BigDecimal("1.18"); // Based on predefined exchange rate
//        assertEquals(expectedAmount, convertedAmount.getAmount(), "Converted amount should match expected exchange rate");
//        assertEquals(Currency.getInstance("USD"), convertedAmount.getCurrency(), "Currency should change to USD");
//    }
//    @Test
//    public void testConvertZeroAmount() {
//        Money amount = new Money(BigDecimal.ZERO, Currency.getInstance("INR"));
//        Money convertedAmount = currencyConversionService.convert(amount, "INR", "USD");
//
//        assertEquals(BigDecimal.ZERO, convertedAmount.getAmount(), "Zero amount should remain zero after conversion");
//        assertEquals(Currency.getInstance("USD"), convertedAmount.getCurrency(), "Currency should still change");
//    }
//    @Test
//    public void testConvertWithUnsupportedCurrency() {
//        Money amount = new Money(new BigDecimal("100.00"), Currency.getInstance("INR"));
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            currencyConversionService.convert(amount, "INR", "XYZ");
//        });
//
//        assertEquals("Unsupported currency:XYZ", exception.getMessage());
//    }



}
