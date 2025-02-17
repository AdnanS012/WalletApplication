package com.example.WalletApp.Domain;

import javax.persistence.Embeddable;

@Embeddable
public class Wallet {
    private Money balance;

     public Wallet() {
        this.balance = Money.Zero;
    }
    public void deposit(Money amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(Money amount) {
        this.balance = this.balance.subtract(amount);
    }

    public boolean canWithdraw(Money amount){
        return this.balance.isGreaterThanOrEqual(amount);
    }
    public Money provideBalance() {
        return this.balance; // This method is specific to controlled access
    }
}
