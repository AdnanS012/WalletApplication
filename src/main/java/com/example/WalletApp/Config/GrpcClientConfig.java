package com.example.WalletApp.Config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
        @Bean
        public ManagedChannel grpcChannel() {
            return ManagedChannelBuilder.forAddress("localhost", 50051)
                    .usePlaintext()  // Disable TLS for local testing
                    .build();
        }
    }

