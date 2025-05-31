package org.example.transactions.model;
import java.util.Date;

public class WithdrawalLogEntry extends LogEntry {
    private double amount;

    public WithdrawalLogEntry(Date timestamp, String user, double amount) {
        super(timestamp, user);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "[" + formatTimestamp(getTimestamp()) + "] " +
                getUser() + " withdrew " + amount;
    }
}
