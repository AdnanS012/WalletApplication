package com.example.WalletApp.Service;

import com.example.WalletApp.Domain.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pb.CurrencyConverterOuterClass;

import java.math.BigDecimal;
import java.util.Currency;

@Service
public class CurrencyConversionService {

    private final GrpcCurrencyConversionClient grpcClient;

    @Autowired
    public CurrencyConversionService(GrpcCurrencyConversionClient grpcClient) {
        this.grpcClient = grpcClient;
    }

    public Money convert(Money amount, String fromCurrency, String toCurrency) {
        System.out.println("🔄 Converting currency from " + fromCurrency + " to " + toCurrency + " | Amount: " + amount);

        if (amount.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return new Money(BigDecimal.ZERO, Currency.getInstance(toCurrency));
        }

        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        // Call gRPC to get the converted money
        CurrencyConverterOuterClass.Money convertedMoney = grpcClient.convertCurrency(
                 fromCurrency, toCurrency,amount.getAmount().doubleValue());
        System.out.println("✅ Converted Amount: " + convertedMoney);

        return new Money(
                BigDecimal.valueOf(convertedMoney.getAmount()),
                Currency.getInstance(convertedMoney.getCurrency())
        );
    }

}
