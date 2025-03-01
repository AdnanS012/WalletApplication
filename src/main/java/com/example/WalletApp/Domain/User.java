package com.example.WalletApp.Domain;

import com.example.WalletApp.Service.CurrencyConversionService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;

@Entity
@Table(name="userstbl")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increment id
    private Long id;
    @Column(nullable = false,unique = true)
    private String username;
    @Column(nullable = false)
    private String password;


    @Embedded
    private Wallet wallet;
    // No-argument constructor
    protected User() {
        this.wallet = new Wallet();
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
        System.out.println("📥 Depositing amount: " + amount + " into wallet with currency: " + this.wallet.provideBalance().getCurrency());

        wallet.deposit(amount);
        System.out.println("✅ New Wallet Balance: " + this.wallet.provideBalance());

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

    public Long identify() {
        return this.id;
    }
    @JsonProperty("walletBalance")
    public Money getBalanceForResponse() {
        return this.wallet.provideBalance();
    }

    public void updateWalletCurrency(Currency newCurrency, CurrencyConversionService currencyConversionService) {
        this.wallet.updateCurrency(newCurrency,currencyConversionService);
    }



    @Override
    public String getUsername() {
        return username;
    }
    public Currency getCurrency() {
        return wallet.getCurrency();
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
