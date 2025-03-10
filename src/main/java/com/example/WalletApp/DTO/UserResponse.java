package com.example.WalletApp.DTO;

import com.example.WalletApp.Domain.Money;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse {
    private Long id;
    @JsonProperty("username")
    private String username;
    private String message;

    @JsonProperty("walletBalance")
    private Money walletBalance;

    public UserResponse(Long id,String username, Money walletBalance) {
        this.id = id;
        this.username = username;
        this.walletBalance = walletBalance;
    }

    public UserResponse() {

    }

    public Long getId() {
        return id;
    }
    public String username() {
        return username;
    }
    @JsonProperty("message")
    public String getMessages() {
      return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
