package com.example.WalletApp.Config;

import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Repository.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Seeder {
    @Bean
    public CommandLineRunner seedAdminUser(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin") == null) {
                String encodedPassword = passwordEncoder.encode("admin");
                User admin = new User("admin", encodedPassword);
                userRepository.save(admin);
                System.out.println("Admin user created: username=admin, password=admin");
            }else{
                System.out.println("Admin already exist in db");
            }
        };
    }
}
