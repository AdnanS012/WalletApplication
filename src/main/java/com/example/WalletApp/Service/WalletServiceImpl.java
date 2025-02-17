package com.example.WalletApp.Service;

import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final IUserRepository userRepository;

@Autowired
    public WalletServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deposit(Long userId, Money amount){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.depositToWallet(amount);
        userRepository.save(user);    }

    @Override
    public void withdraw(Long userId, Money amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.canWithdrawFromWallet(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        user.withdrawFromWallet(amount);
        userRepository.save(user);

    }

    @Override
    public Money getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getBalanceForResponse();
    }
}
