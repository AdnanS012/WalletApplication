package com.example.WalletApp.Service;

import com.example.WalletApp.Domain.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

@Service
public class HttpCurrencyConversionClient {

 public RestTemplate restTemplate;
    @Value("${conversion.service.url}")
    public String conversionServiceUrl;


    public HttpCurrencyConversionClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }



    public Money convertCurrency(String from, String to, double amount) {
        // Create request body matching the expected JSON format
        Map<String, Object> money = new HashMap<>();
        money.put("amount", amount);
        money.put("currency", from);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", money);
        requestBody.put("to_currency", to);

        // Call the HTTP API
        ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                conversionServiceUrl, requestBody, (Class<Map<String, Object>>)(Class<?>)Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to convert currency: HTTP status " + response.getStatusCodeValue());
        }
        // Extract response data
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("converted_amount")) {
            throw new RuntimeException("Invalid response from currency conversion service");
        }

        Object convertedAmountObj = responseBody.get("converted_amount");
        if (!(convertedAmountObj instanceof Map)) {
            throw new RuntimeException("Invalid response structure: converted_amount is not a map");
        }

        Map<String, Object> convertedAmount = (Map<String, Object>) convertedAmountObj;

        // Convert to Money object and return
        return new Money(
                new BigDecimal(convertedAmount.get("amount").toString()),
                Currency.getInstance(convertedAmount.get("currency").toString())
        );
    }


}
