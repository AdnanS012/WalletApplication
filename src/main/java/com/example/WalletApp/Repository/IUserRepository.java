package com.example.WalletApp.Repository;

import com.example.WalletApp.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);// To check if a user already exists
    User findByUsername(String username);
}
