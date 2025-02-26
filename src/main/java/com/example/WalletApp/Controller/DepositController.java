package com.example.WalletApp.Controller;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}")
public class DepositController {

    private final WalletService walletService;

    @Autowired
    public DepositController(WalletService walletService) {
        this.walletService = walletService;
    }


    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@PathVariable Long userId, @RequestBody Money amount) {
        try {
            System.out.println("Received deposit request: " + amount);
            System.out.println("Amount: " + amount.getAmount() + ", Currency: " + amount.getCurrency());

            walletService.deposit(userId, amount);
            return ResponseEntity.ok("Deposit successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
