package com.example.WalletApp.Controller;

import com.example.WalletApp.DTO.TransactionResponse;
import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class TransactionController {
    private final WalletService walletService;

    @Autowired
    public TransactionController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{userId}/transactions/{receiverId}")
    public ResponseEntity<String> transferMoney(@PathVariable Long userId, @PathVariable Long receiverId, @RequestBody Money amount) {
        try {
            walletService.transferMoney(userId, receiverId, amount);
            return ResponseEntity.ok("Transfer successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (RuntimeException e) {  //  Catches DB failures or unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity <TransactionResponse> getTransactions(@PathVariable Long userId) {
        try {
            TransactionResponse transactions = walletService.getTransactionById(userId);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

@GetMapping("/transactions")
public ResponseEntity<List<TransactionResponse>> getTransactions() {
    try {
        List<TransactionResponse> transactions = walletService.getTransactions();
        return ResponseEntity.ok(transactions);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(null);
    }
}
}
