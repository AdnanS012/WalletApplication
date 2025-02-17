package Controller;

import Domain.User;
import Domain.Money;
import Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            userService.registerUser(user.getUsername(), user.getPassword());
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");

        }

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        boolean authenticated = userService.authenticateUser(user.getUsername(),user.getPassword());
        return authenticated ? ResponseEntity.ok("Login successful!") : ResponseEntity.status(401).body("Invalid credentials");

    }

    @PostMapping("/{username}/wallet/deposit")
    public ResponseEntity<String> deposit(@PathVariable String username, @RequestBody Money amount) {
        try {
            userService.deposit(username, amount);
            return ResponseEntity.ok("Deposit successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/{username}/wallet/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable String username, @RequestBody Money amount) {
        try {
            userService.withdraw(username, amount);
            return ResponseEntity.ok("Withdrawal successful!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }


    }
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}
