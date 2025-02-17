package Domain;

public class Wallet {
    private Money balance;

     public Wallet() {
        this.balance = Money.Zero;
    }
    public void deposit(Money amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(Money amount) {
        this.balance = this.balance.subtract(amount);
    }

    public boolean canWithdraw(Money amount){
        return this.balance.isGreaterThanOrEqual(amount);
    }
}
