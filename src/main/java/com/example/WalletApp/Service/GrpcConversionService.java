package com.example.WalletApp.Service;

import org.springframework.stereotype.Service;

@Service
public class GrpcConversionService {
    private final GrpcCurrencyConversionClient grpcCurrencyConversionClient;

    public GrpcConversionService(GrpcCurrencyConversionClient grpcCurrencyConversionClient) {
        this.grpcCurrencyConversionClient = grpcCurrencyConversionClient;
    }

    public double convertCurrency(String from, String to, double amount) {
        return grpcCurrencyConversionClient.convertCurrency(from, to, amount);
    }
}
