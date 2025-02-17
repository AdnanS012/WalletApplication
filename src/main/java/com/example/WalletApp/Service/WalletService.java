package com.example.WalletApp.Service;

import com.example.WalletApp.Domain.Money;

public interface WalletService {
    void deposit(Long userId, Money amount);
    void withdraw(Long userId, Money amount);
    Money getBalance(Long userId);

}
