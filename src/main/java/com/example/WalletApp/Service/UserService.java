package com.example.WalletApp.Service;


import com.example.WalletApp.DTO.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    void registerUser(String username, String password);
    boolean authenticateUser(String username, String password);
     UserResponse getUserByUsername(String username);
    UserResponse getUserById(Long id);
}
