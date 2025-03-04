package com.example.WalletApp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.CurrencyConversionService;
import com.example.WalletApp.Service.HttpCurrencyConversionClient;
//import okhttp3.mockwebserver.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

class CurrencyConversionServiceTest {

    private CurrencyConversionService currencyConversionService;
    private HttpCurrencyConversionClient httpClient;

    @Mock
    private RestTemplate restTemplate; // Mock RestTemplate

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        // Mock RestTemplateBuilder to return our mock RestTemplate
        RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        // Initialize HttpCurrencyConversionClient with mocked RestTemplateBuilder
        httpClient = new HttpCurrencyConversionClient(restTemplateBuilder);
        httpClient.conversionServiceUrl = "http://localhost:8080/v1/currency_converter/convert"; // Set fake URL

        // Initialize CurrencyConversionService
        currencyConversionService = new CurrencyConversionService(httpClient);
    }

    @Test
    void shouldConvertCurrencySuccessfully() {
        // Given: Input money and expected response
        Money inputMoney = new Money(BigDecimal.valueOf(100.0), Currency.getInstance("USD"));
        Money expectedMoney = new Money(BigDecimal.valueOf(8500.0), Currency.getInstance("INR"));

        // Mock API Response
        Map<String, Object> mockResponse = new HashMap<>();
        Map<String, Object> convertedAmount = new HashMap<>();
        convertedAmount.put("amount", 8500.0);
        convertedAmount.put("currency", "INR");
        mockResponse.put("converted_amount", convertedAmount);

        // Mock HTTP call
        when(restTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // When: Conversion is requested
        Money result = currencyConversionService.convert(inputMoney, "USD", "INR");

        // Then: Verify conversion
        assertNotNull(result);
        assertEquals(expectedMoney.getAmount(), result.getAmount());
        assertEquals(expectedMoney.getCurrency(), result.getCurrency());

        // Verify HTTP call was made with correct URL and request body
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).postForEntity(urlCaptor.capture(), any(), any());

        assertEquals("http://localhost:8080/v1/currency_converter/convert", urlCaptor.getValue());
    }
    @Test
    void shouldReturnZeroForZeroAmount() {
        Money inputMoney = new Money(BigDecimal.ZERO, Currency.getInstance("USD"));
        Money result = currencyConversionService.convert(inputMoney, "USD", "INR");

        assertEquals(BigDecimal.ZERO, result.getAmount());
        assertEquals(Currency.getInstance("INR"), result.getCurrency());

        verifyNoInteractions(restTemplate); // Ensure no HTTP call was made
    }

    @Test
    void shouldReturnSameMoneyForSameCurrency() {
        Money inputMoney = new Money(BigDecimal.valueOf(500.0), Currency.getInstance("EUR"));
        Money result = currencyConversionService.convert(inputMoney, "EUR", "EUR");

        assertEquals(inputMoney, result);

        verifyNoInteractions(restTemplate); // Ensure no HTTP call was made
    }
    @Test
    void shouldThrowExceptionOnHttpFailure() {
        Money inputMoney = new Money(BigDecimal.valueOf(100.0), Currency.getInstance("USD"));

        when(restTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        Exception exception = assertThrows(RuntimeException.class, () ->
                currencyConversionService.convert(inputMoney, "USD", "INR"));

        assertTrue(exception.getMessage().contains("Failed to convert currency"));

        verify(restTemplate).postForEntity(anyString(), any(), any());
    }

    @Test
    void shouldThrowExceptionOnInvalidResponse() {
        Money inputMoney = new Money(BigDecimal.valueOf(100.0), Currency.getInstance("USD"));

        Map<String, Object> invalidResponse = new HashMap<>(); // Missing "converted_amount"
        when(restTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(ResponseEntity.ok(invalidResponse));

        Exception exception = assertThrows(RuntimeException.class, () ->
                currencyConversionService.convert(inputMoney, "USD", "INR"));

        assertTrue(exception.getMessage().contains("Invalid response"));

        verify(restTemplate).postForEntity(anyString(), any(), any());
    }

}
