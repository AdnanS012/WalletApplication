package com.example.WalletApp.Service;

import com.example.WalletApp.DTO.TransactionResponse;
import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Domain.Transaction;

import java.util.List;

public interface WalletService {
    void deposit(Long userId, Money amount);
    void withdraw(Long userId, Money amount);
    Money getBalance(Long userId);
    void transferMoney(Long senderId, Long receiverId, Money amount);
    List<TransactionResponse> getTransactions();
    TransactionResponse getTransactionById(Long id);
}
