package com.example.WalletApp.Service;

import org.springframework.stereotype.Service;
import pb.CurrencyConverterOuterClass;

@Service
public class GrpcConversionService {
    private final GrpcCurrencyConversionClient grpcCurrencyConversionClient;

    public GrpcConversionService(GrpcCurrencyConversionClient grpcCurrencyConversionClient) {
        this.grpcCurrencyConversionClient = grpcCurrencyConversionClient;
    }

    public CurrencyConverterOuterClass.Money convertCurrency(String fromCurrency, String toCurrency,double amount) {
        return grpcCurrencyConversionClient.convertCurrency(fromCurrency, toCurrency,amount);
    }

}
