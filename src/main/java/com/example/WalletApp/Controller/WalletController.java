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
@RequestMapping("/users/{userId}/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@PathVariable Long userId, @RequestBody Money amount) {
        try {
            walletService.deposit(userId, amount);
            return ResponseEntity.ok("Deposit successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable Long userId, @RequestBody Money amount) {
        try {
            walletService.withdraw(userId, amount);
            return ResponseEntity.ok("Withdrawal successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Money> getBalance(@PathVariable Long userId) {
        try {
            Money balance = walletService.getBalance(userId);
            return ResponseEntity.ok(balance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/transaction/{receiverId}")
    public ResponseEntity<String> transfer(@PathVariable Long userId, @PathVariable Long receiverId, @RequestBody Money amount) {
        try {
            walletService.transferMoney(userId, receiverId, amount);
            return ResponseEntity.ok("Transfer successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (RuntimeException e) {  //  Catches DB failures or unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long userId) {
        try {
            List<TransactionResponse> transactions = walletService.getTransactions(userId);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


}
