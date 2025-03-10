package com.example.WalletApp.Service;

import com.example.WalletApp.DTO.TransactionResponse;
import com.example.WalletApp.Domain.Money;
import com.example.WalletApp.Domain.Transaction;
import com.example.WalletApp.Domain.User;
import com.example.WalletApp.Enum.TransactionType;
import com.example.WalletApp.Repository.IUserRepository;
import com.example.WalletApp.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final IUserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private  final CurrencyConversionService currencyConversionService;

@Autowired
    public WalletServiceImpl(IUserRepository userRepository,TransactionRepository transactionRepository,CurrencyConversionService currencyConversionService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.currencyConversionService = currencyConversionService;
    }

    @Override
    public void deposit(Long userId, Money amount){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.depositToWallet(amount);
        userRepository.save(user);
      transactionRepository.save(new Transaction(user, TransactionType.DEPOSIT,amount));
}

    @Override
    public void withdraw(Long userId, Money amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.canWithdrawFromWallet(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        user.withdrawFromWallet(amount);
        userRepository.save(user);
        transactionRepository.save(new Transaction(user, TransactionType.WITHDRAWAL,amount));

    }

    @Override
    public Money getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getBalanceForResponse();
    }

     @Transactional
    public void transferMoney(Long senderId, Long receiverId, Money amount) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException(" Sender not found!"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException(" Receiver not found!"));
         System.out.println(" Transfer Request - Sender: " + sender.getUsername() + ", Receiver: " + receiver.getUsername());
         System.out.println("   - Sender Balance: " + sender.getBalanceForResponse());
         System.out.println("   - Receiver Balance: " + receiver.getBalanceForResponse());
         System.out.println("   - Transfer Amount: " + amount);
        Money convertedAmount;

        //  Handle transfers within the same currency (skip unnecessary conversion)
        if (sender.getCurrency().equals(receiver.getCurrency())) {
            convertedAmount = amount;
        } else {
            //Convert the currency
            convertedAmount = currencyConversionService.convert(amount,
                    sender.getCurrency().getCurrencyCode(),
                    receiver.getCurrency().getCurrencyCode());
            System.out.println("💱 Converted Amount: " + convertedAmount);
            receiver.updateWalletCurrency(convertedAmount.getCurrency(), currencyConversionService);

            //**Update receiver's wallet currency before deposit
        }

        //  Withdraw from sender
        if (!sender.canWithdrawFromWallet(amount)) {
            throw new IllegalArgumentException("🚨 Insufficient balance!");
        }
        sender.withdrawFromWallet(amount);
         System.out.println("✅ Sender new balance: " + sender.getBalanceForResponse());

        //  Deposit to receiver
        receiver.depositToWallet(convertedAmount);
         //  Save updates
        userRepository.save(sender);
        userRepository.save(receiver);

        //  Record transactions

        transactionRepository.save(new Transaction(sender, TransactionType.TRANSFER_OUT, amount));
        transactionRepository.save(new Transaction(receiver, TransactionType.TRANSFER_IN, convertedAmount));
 }


    @Override
    public List<TransactionResponse> getTransactions() {
   List<Transaction> transactions = transactionRepository.findAll();
        if (transactions.isEmpty()) {
            throw new IllegalArgumentException("No transactions found");
        }
   return transactions.stream()
           .map(TransactionResponse::from)
           .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        return TransactionResponse.from(transaction);
    }
}
