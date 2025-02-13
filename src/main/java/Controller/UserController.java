package Controller;

import Money.Money;
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
    public ResponseEntity<String> register(@RequestParam String username, @RequestParam String password) {
        try {
            userService.registerUser(username, password);
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");

        }

    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        boolean authenticated = userService.authenticateUser(username, password);
        return authenticated ? "Login successful!" : "Invalid credentials!";

    }

    @PostMapping("/{username}/deposit")
    public ResponseEntity<String> deposit(@PathVariable String username, @RequestBody Money amount) {
        try {
            userService.deposit(username, amount);
            return ResponseEntity.ok("Deposit successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/{username}/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable String username, @RequestBody Money amount) {
        try {
            userService.withdraw(username, amount);
            return ResponseEntity.ok("Withdrawal successful!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }


    }
}
