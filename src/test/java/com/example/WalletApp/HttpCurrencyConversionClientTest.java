package com.example.WalletApp;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.HttpCurrencyConversionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpCurrencyConversionClientTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HttpCurrencyConversionClient currencyClient;

    private final String conversionServiceUrl = "http://localhost:8080/v1/currency_converter/convert";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock RestTemplateBuilder to return the mocked RestTemplate
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        // Manually inject conversionServiceUrl since @Value won't be resolved in tests
        currencyClient = new HttpCurrencyConversionClient(restTemplateBuilder);
        currencyClient.conversionServiceUrl = conversionServiceUrl;  // Manually set the URL
    }

    @Test
    void shouldConvertCurrencySuccessfully() {
        // Given
        String fromCurrency = "USD";
        String toCurrency = "INR";
        double amount = 100.0;

        // Mock API Response
        Map<String, Object> convertedAmount = new HashMap<>();
        convertedAmount.put("amount", 8300.50);
        convertedAmount.put("currency", "INR");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("converted_amount", convertedAmount);

        when(restTemplate.postForEntity(eq(conversionServiceUrl), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // When
        Money result = currencyClient.convertCurrency(fromCurrency, toCurrency, amount);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(8300.50), result.getAmount());
        assertEquals(Currency.getInstance("INR"), result.getCurrency());
    }

    @Test
    void shouldThrowExceptionWhenApiReturnsNullResponse() {
        // Given
        when(restTemplate.postForEntity(eq(conversionServiceUrl), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () ->
                currencyClient.convertCurrency("USD", "INR", 100.0));

        assertEquals("Invalid response from currency conversion service", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForMalformedApiResponse() {
        // Given
        Map<String, Object> malformedResponse = new HashMap<>(); // Missing "converted_amount" field

        when(restTemplate.postForEntity(eq(conversionServiceUrl), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(malformedResponse, HttpStatus.OK));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () ->
                currencyClient.convertCurrency("USD", "INR", 100.0));

        assertEquals("Invalid response from currency conversion service", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidResponseStructure() {
        // Given
        Map<String, Object> invalidResponse = new HashMap<>();
        invalidResponse.put("converted_amount", "INVALID_DATA"); // Not a map

        when(restTemplate.postForEntity(eq(conversionServiceUrl), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(invalidResponse, HttpStatus.OK));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () ->
                currencyClient.convertCurrency("USD", "INR", 100.0));

        assertEquals("Invalid response structure: converted_amount is not a map", exception.getMessage());
    }
    @Test
    void shouldThrowExceptionForNon200HttpStatus() {
        // Given
        when(restTemplate.postForEntity(eq(conversionServiceUrl), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () ->
                currencyClient.convertCurrency("USD", "INR", 100.0));

        assertEquals("Failed to convert currency: HTTP status 400", exception.getMessage());
    }


}
