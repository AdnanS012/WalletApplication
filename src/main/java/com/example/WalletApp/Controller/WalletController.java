package com.example.WalletApp.Controller;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/users/{userId}/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
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




}
