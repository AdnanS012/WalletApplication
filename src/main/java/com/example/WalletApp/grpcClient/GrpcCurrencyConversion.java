package com.example.WalletApp.grpcClient;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import pb.CurrencyConverterGrpc;
import pb.CurrencyConverterOuterClass;

@Service
public class GrpcCurrencyConversion {

    @GrpcClient("currency-converter")
    private CurrencyConverterGrpc.CurrencyConverterBlockingStub currencyConverterStub;

    public double convertCurrency(String from, String to, double amount){
        CurrencyConverterOuterClass.ConvertRequest request = CurrencyConverterOuterClass.ConvertRequest.newBuilder()
                .setFromCurrency(from)
                .setToCurrency(to)
                .setAmount(amount)
                .build();
        CurrencyConverterOuterClass.ConvertResponse response = currencyConverterStub.convert(request);
        return response.getConvertedAmount();
    }
}
