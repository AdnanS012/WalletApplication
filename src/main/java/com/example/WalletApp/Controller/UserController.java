package com.example.WalletApp.Controller;

import com.example.WalletApp.DTO.UserResponse;
import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping //create a new user with username and password
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            userService.registerUser(user.getUsername(), user.getPassword());
            return ResponseEntity.ok("User registered successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");

        }

    }

    @PostMapping("/auth/sign-in")
    public ResponseEntity<String> login(@RequestBody User user) {
        boolean authenticated = userService.authenticateUser(user.getUsername(),user.getPassword());
        return authenticated ? ResponseEntity.ok("Login successful!") : ResponseEntity.status(401).body("Invalid credentials");

    }

    @PostMapping("/{id}/wallets/deposit")
    public ResponseEntity<String> deposit(@PathVariable Long id, @RequestBody Money amount) {
        try {
            userService.deposit(id, amount);
            return ResponseEntity.ok("Deposit successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PostMapping("/{id}/wallet/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable Long id, @RequestBody Money amount) {
        try {
            userService.withdraw(id, amount);
            return ResponseEntity.ok("Withdrawal successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserResponse> userDetailsByName(@PathVariable String username) {
        try {
            UserResponse userResponse = userService.getUserByUsername(username);
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> userDetailsById(@PathVariable Long id) {
        try {
            UserResponse userResponse = userService.getUserById(id);
            System.out.println(userResponse);
            log.info("User response :{}",userResponse);
           return ResponseEntity.ok(userResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}


