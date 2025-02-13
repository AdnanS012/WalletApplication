package Service;


import Money.Money;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
@Service
public interface UserService extends UserDetailsService {
    void registerUser(String username, String password);
    boolean authenticateUser(String username, String password);
    void deposit(String username, Money amount);
    void withdraw(String username, Money amount);  //New method for withdrawal

}
