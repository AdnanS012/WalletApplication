package com.example.WalletApp.Controller;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}")
public class WithdrawController {
private final WalletService walletService;

@Autowired
    public WithdrawController(WalletService walletService) {
        this.walletService = walletService;
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
}
