package com.example.WalletApp.DTO;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Domain.Transaction;
import com.example.WalletApp.Enum.TransactionType;

import java.time.LocalDateTime;

public class TransactionResponse {
    private Long id;
    private Long userId;
    private Money amount;
    private TransactionType type;
    private LocalDateTime timestamp;

    public TransactionResponse(Long id, Long userId, Money amount, TransactionType type, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Money getAmount(){
        return amount;
    }

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getUser().identify(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTimestamp()
        );
    }

}
