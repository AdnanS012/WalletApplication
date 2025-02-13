package Money;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public class Money {
    private static final Currency   STANDARD_CURRENCY = Currency.getInstance("INR");
    public static final Money Zero = new Money(BigDecimal.ZERO);
    private BigDecimal amount;

    protected Money(){} //Required by JPA

    public Money(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        this.amount = amount;
    }

    public Money add(Money money){
        if (money == null) {
            throw new IllegalArgumentException("Money cannot be null");
        }
        return new Money(this.amount.add(money.amount));
    }

    public Money subtract(Money money){
        if (money == null) {
            throw new IllegalArgumentException("Money cannot be null");
        }
        return new Money(this.amount.subtract(money.amount));
    }

    public boolean isGreaterThanOrEqual(Money money){
        return  this.amount.compareTo(money.amount) >= 0;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Money)) return false;
        Money money = (Money) obj;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
    @Override
    public String toString() {
        return STANDARD_CURRENCY.getSymbol() + " " + amount;
    }



}


