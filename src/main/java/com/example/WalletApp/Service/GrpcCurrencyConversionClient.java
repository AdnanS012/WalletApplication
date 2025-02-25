package com.example.WalletApp.Service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;
import pb.CurrencyConverterGrpc;
import pb.CurrencyConverterOuterClass;

@Service
public class GrpcCurrencyConversionClient {


    private CurrencyConverterGrpc.CurrencyConverterBlockingStub currencyConverterStub;

    public GrpcCurrencyConversionClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        currencyConverterStub = CurrencyConverterGrpc.newBlockingStub(channel);
    }



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
