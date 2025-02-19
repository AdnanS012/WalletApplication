package com.example.WalletApp.Domain;

import com.example.WalletApp.Service.CurrencyConversionService;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Currency;

@Embeddable
public class Wallet {
    private Money balance;
     @Column(nullable = false)
     private  Currency currency;

     public Wallet(Money balance) {
        this.balance = balance!= null ? balance : Money.Zero;
        this.currency = balance!= null ? balance.getCurrency() : Currency.getInstance("INR");
    }

    protected Wallet(){
         this.balance = Money.Zero;
        this.currency = Currency.getInstance("INR"); //Default currency if not set

    } //Required by JPA

    public static Wallet createDefaultWallet(){
        return new Wallet();
    }

    public void deposit(Money amount) {
         validateCurrency(amount);
        System.out.println("🔹 Before Deposit - Wallet Balance: " + this.balance);
        System.out.println("🔹 Depositing Amount: " + amount);

        this.balance = this.balance.add(amount);
        System.out.println("✅ After Deposit - Wallet Balance: " + this.balance);

    }

    public void withdraw(Money amount) {
         validateCurrency(amount);
        this.balance = this.balance.subtract(amount);
    }

    public boolean canWithdraw(Money amount){
         validateCurrency(amount);
        return this.balance.isGreaterThanOrEqual(amount);
    }
    public Money provideBalance() {
        return this.balance; // This method is specific to controlled access
    }


    public Currency getCurrency() {
        return this.balance.getCurrency();
    }
    private void validateCurrency(Money amount) {
        if (!this.getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch in wallet");
        }
    }

    public void updateCurrency(Currency newCurrency, CurrencyConversionService currencyConversionService) {
        if (!this.currency.equals(newCurrency)) {
            System.out.println("🔄 Updating Wallet Currency from " + this.currency + " to " + newCurrency);

            // 🛑 Fix: Only convert if balance is NOT zero
            if (!this.balance.equals(Money.Zero)) {
                this.balance = currencyConversionService.convert(this.balance, this.currency.getCurrencyCode(), newCurrency.getCurrencyCode());
            }

            this.currency = newCurrency;
        }
    }


}

