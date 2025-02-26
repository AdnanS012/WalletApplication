package com.example.WalletApp.Domain;

import com.example.WalletApp.CurrencyDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Embeddable
public class Money {
    private static final Currency   STANDARD_CURRENCY = Currency.getInstance("INR");
    public static final Money Zero = new Money(BigDecimal.ZERO,STANDARD_CURRENCY);

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @JsonDeserialize(using = CurrencyDeserializer.class)  //Use the custom deserializer
    private Currency currency;

    protected Money(){
        this.amount = BigDecimal.ZERO;
        this.currency = STANDARD_CURRENCY; //Default currency if not set
    } //Required by JPA

    @JsonCreator
    public Money(@JsonProperty("amount")BigDecimal amount,@JsonProperty("currency") Currency currency){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be zero or negative");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public Money add(Money money){
        if (money == null) {
            throw new IllegalArgumentException("Money cannot be null");
        }
        validateCurrency(money);
        Money result = new Money(this.amount.add(money.amount), this.currency);
        return result;

    }

    public Money subtract(Money money){
        if (money == null) {
            throw new IllegalArgumentException("Money cannot be null");
        }
        validateCurrency(money);
        return new Money(this.amount.subtract(money.amount),this.currency);
    }

    private void validateCurrency(Money money){
        System.out.println("Wallet Currency: " + this.getCurrency());
        System.out.println("Incoming Money Currency: " + money.currency); // Log incoming money currency

        if (!this.currency.equals(money.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch"+ this.currency + " vs" + money.getCurrency());
        }
    }

    public boolean isGreaterThanOrEqual(Money money){
        return  this.amount.compareTo(money.amount) >= 0;
    }

    public Currency getCurrency() {
        return currency;
    }


    @JsonProperty("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return currency.getSymbol() + " " + amount;
    }



}


