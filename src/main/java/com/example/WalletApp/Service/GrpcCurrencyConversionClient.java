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



    public CurrencyConverterOuterClass.Money convertCurrency(String from, String to, double amount) {
        // Create Money object
        CurrencyConverterOuterClass.Money money = CurrencyConverterOuterClass.Money.newBuilder()
                .setAmount(amount)
                .setCurrency(from)  // Set source currency inside Money
                .build();

        // Create ConvertRequest
        CurrencyConverterOuterClass.ConvertRequest request = CurrencyConverterOuterClass.ConvertRequest.newBuilder()
                .setAmount(money)  // Set the Money object
                .setToCurrency(to)  // Set target currency
                .build();

        // Call gRPC server
        CurrencyConverterOuterClass.ConvertResponse response = currencyConverterStub.convert(request);

        // Extract converted amount from Money object inside response
        return response.getConvertedAmount();
    }

}
