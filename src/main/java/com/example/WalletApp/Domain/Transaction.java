package com.example.WalletApp.Domain;

import com.example.WalletApp.Enum.TransactionType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Embedded
    private Money amount;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    protected Transaction() {}

    public Transaction(User user, TransactionType type, Money amount) {
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public Money getAmount(){
        return amount;
    }

    public TransactionType getType() {
        return type;
    }
    public LocalDateTime getTimestamp() { return timestamp; }


}
