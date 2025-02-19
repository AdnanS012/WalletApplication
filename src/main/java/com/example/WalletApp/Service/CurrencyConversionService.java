package com.example.WalletApp.Service;

import com.example.WalletApp.Domain.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyConversionService {
    private static final Map<String, BigDecimal> exchangeRates  = new HashMap<>();

    static{
        exchangeRates.put("USD", BigDecimal.valueOf(1.0));
        exchangeRates.put("EUR", BigDecimal.valueOf(0.85));
        exchangeRates.put("INR",BigDecimal.valueOf(85));
    }

    public Money convert(Money amount,String fromCurrency,String toCurrency){
        if (amount.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return new Money(BigDecimal.ZERO, Currency.getInstance(toCurrency));
        }
        if(fromCurrency.equals(toCurrency)){
            return amount;
        }
        if (!isSupportedCurrency(fromCurrency) || !isSupportedCurrency(toCurrency)) {
            throw new IllegalArgumentException("Unsupported currency:" + toCurrency);
        }

        BigDecimal fromRate = exchangeRates.getOrDefault(fromCurrency, BigDecimal.ONE);
        BigDecimal toRate = exchangeRates.getOrDefault(toCurrency, BigDecimal.ONE);

        BigDecimal convertedAmount = amount.getAmount().multiply(toRate).divide(fromRate,2, RoundingMode.HALF_UP);
        System.out.println("Converted Money: " + convertedAmount);

        return new Money(convertedAmount, Currency.getInstance(toCurrency));
    }
    private boolean isSupportedCurrency(String currencyCode) {
        // Check if the currency is supported
        return "USD".equals(currencyCode) || "INR".equals(currencyCode);
    }


}
