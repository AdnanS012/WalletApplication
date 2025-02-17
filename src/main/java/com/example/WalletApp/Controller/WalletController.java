package com.example.WalletApp.Controller;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<String> deposit(@PathVariable Long userId, @RequestBody Money amount) {
        try {
            walletService.deposit(userId, amount);
            return ResponseEntity.ok("Deposit successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable Long userId, @RequestBody Money amount) {
        try {
            walletService.withdraw(userId, amount);
            return ResponseEntity.ok("Withdrawal successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<Money> getBalance(@PathVariable Long userId) {
        try {
            Money balance = walletService.getBalance(userId);
            return ResponseEntity.ok(balance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
