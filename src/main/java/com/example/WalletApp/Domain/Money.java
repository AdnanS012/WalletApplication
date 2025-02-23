package com.example.WalletApp.Domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
        System.out.println("💰 Money Add Called: " + this + " + " + money);
        Money result = new Money(this.amount.add(money.amount), this.currency);

        System.out.println("➡️ Result after Addition: " + result);
        return result;

       // return new Money(this.amount.add(money.amount),this.currency);
    }

    public Money subtract(Money money){
        if (money == null) {
            throw new IllegalArgumentException("Money cannot be null");
        }
        validateCurrency(money);
        return new Money(this.amount.subtract(money.amount),this.currency);
    }

    private void validateCurrency(Money money){
        if (!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
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


