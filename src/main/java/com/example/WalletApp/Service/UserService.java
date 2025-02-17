package com.example.WalletApp.Service;


import com.example.WalletApp.DTO.UserResponse;
import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Domain.Money;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    void registerUser(String username, String password);
    boolean authenticateUser(String username, String password);
    void deposit(Long id, Money amount);
    void withdraw(Long id, Money amount);  //New method for withdrawal
     UserResponse getUserByUsername(String username);
    UserResponse getUserById(Long id);
}
