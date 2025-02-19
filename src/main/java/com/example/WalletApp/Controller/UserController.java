package com.example.WalletApp.Controller;

import com.example.WalletApp.DTO.UserResponse;
import com.example.WalletApp.Domain.User;
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
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }
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


    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserResponse> userDetailsByName(@PathVariable String username) {
        try {
            UserResponse userResponse = userService.getUserByUsername(username);
            userResponse.setMessage("Success");
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException e) {
            UserResponse errorResponse = new UserResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            UserResponse errorResponse = new UserResponse();
            errorResponse.setMessage("Internal Server Error");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> userDetailsById(@PathVariable Long id) {
        try {
            UserResponse userResponse = userService.getUserById(id);
            userResponse.setMessage("Success");
           return ResponseEntity.ok(userResponse);

        } catch (IllegalArgumentException e) {
            UserResponse errorResponse = new UserResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            UserResponse errorResponse = new UserResponse();
            errorResponse.setMessage("Internal Server Error");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}


