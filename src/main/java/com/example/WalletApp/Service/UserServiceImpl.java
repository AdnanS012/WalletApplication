package com.example.WalletApp.Service;

import com.example.WalletApp.DTO.UserResponse;
import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Repository.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
@Transactional
public class UserServiceImpl implements UserService {


private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // ✅ Inject PasswordEncoder


public UserServiceImpl(IUserRepository userRepository,PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
}

    @Transactional
    @Override
    public void registerUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        String encodedPassword = passwordEncoder.encode(password);  // ✅ Encode password
        User newUser = new User(username, encodedPassword);
        userRepository.save(newUser);
    }

    @Override
    public boolean authenticateUser(String username,String password){
    User user = userRepository.findByUsername(username);
        return user != null && passwordEncoder.matches(password,user.getPassword());
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }

@Override
public UserResponse getUserById(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    return new UserResponse(user.identify(), user.getUsername(), user.getBalanceForResponse());
}

}

