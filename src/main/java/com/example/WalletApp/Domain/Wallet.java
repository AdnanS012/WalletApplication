package com.example.WalletApp.Domain;

import com.example.WalletApp.Service.CurrencyConversionService;

import javax.persistence.*;
import java.util.Currency;

@Embeddable
public class Wallet {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "wallet_balance")),
            @AttributeOverride(name = "currency", column = @Column(name = "wallet_currency"))
    })
    private Money balance;
     //@Column(nullable = false)
    // private  Currency currency;

     public Wallet(Money balance) {
         if (balance != null) {
             this.balance = balance;
            // this.currency = balance.getCurrency(); //  Use balance's currency instead of defaulting to INR
         } else {
             this.balance = Money.Zero;
            // this.currency = Money.Zero.getCurrency(); // Default currency from Money.Zero
         }

    }

    protected Wallet(){
         this.balance = Money.Zero;
        //this.currency = Currency.getInstance("INR"); //Default currency if not set

    } //Required by JPA

    public static Wallet createDefaultWallet(){
        return new Wallet();
    }

    public void deposit(Money amount) {
         validateCurrency(amount);
        this.balance = this.balance.add(amount);
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

//    public void updateCurrency(Currency newCurrency, CurrencyConversionService currencyConversionService) {
//        if (!this.currency.equals(newCurrency)) {
//            System.out.println("Updating Wallet Currency from " + this.currency + " to " + newCurrency);
//
//            //  Fix: Only convert if balance is NOT zero
//            if (!this.balance.equals(Money.Zero)) {
//                this.balance = currencyConversionService.convert(this.balance, this.currency.getCurrencyCode(), newCurrency.getCurrencyCode());
//            }
//
//            this.currency = newCurrency;
//        }
//    }
public void updateCurrency(Currency newCurrency, CurrencyConversionService currencyConversionService) {
    if (!this.balance.getCurrency().equals(newCurrency)) {
        System.out.println("Updating Wallet Currency from " + this.balance.getCurrency() + " to " + newCurrency);

        if (!this.balance.equals(Money.Zero)) {  // Only convert if balance isn't zero
            this.balance = currencyConversionService.convert(
                    this.balance,
                    this.balance.getCurrency().getCurrencyCode(), //  Fetch currency from Money
                    newCurrency.getCurrencyCode()
            );
        }

        //  Now, the balance object already holds the new currency
    }
}


}

