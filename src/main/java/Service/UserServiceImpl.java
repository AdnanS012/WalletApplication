package Service;

import Domain.User;
import UserRepository.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import Money.Money;

import java.util.Collections;
@Service
@Transactional
public class UserServiceImpl implements UserService {


private final IUserRepository userRepository;

@Autowired
public UserServiceImpl(IUserRepository userRepository) {
    this.userRepository = userRepository;
}

    @Transactional
    @Override
    public void registerUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        Domain.User newUser = new Domain.User(username, password);
        userRepository.save(newUser);
    }

    @Override
    public boolean authenticateUser(String username,String password){
    User user = userRepository.findByUsername(username);
        return user != null && user.authenticate(password);
    }
    @Override
    public void deposit(String username, Money amount) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.depositToWallet(amount);  // ✅ Calls domain logic
        userRepository.save(user);  // ✅ Persist changes
    }

    @Override
    public void withdraw(String username, Money amount) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.canWithdrawFromWallet(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        user.withdrawFromWallet(amount);  // ✅ Calls domain logic
        userRepository.save(user);  // ✅ Persist changes
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Domain.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
    @Override
    public User getUserByUsername(String username) {
        UserDetails userDetails = loadUserByUsername(username);
        return userRepository.findByUsername(userDetails.getUsername());
    }

}

