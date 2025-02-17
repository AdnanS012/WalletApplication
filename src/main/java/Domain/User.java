package Domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name="userstbl")
public class User implements UserDetails {

    @Id
    @Column(nullable = false,unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Embedded
    private Wallet wallet;
    // No-argument constructor
    public User() {

    }
    public User(String username, String password) {
        if(username==null || username.trim().isEmpty()){
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if(password ==null || password.trim().isEmpty()){
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.username = username;
        this.password = password;
        this.wallet = new Wallet();
    }

    public boolean authenticate(String candidatePassword){
        return  this.password.equals(candidatePassword);
    }

    public void depositToWallet(Money amount){
        wallet.deposit(amount);
    }

    public void withdrawFromWallet(Money amount){
        wallet.withdraw(amount);
    }

    public boolean canWithdrawFromWallet(Money amount){
        return wallet.canWithdraw(amount);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }



}
