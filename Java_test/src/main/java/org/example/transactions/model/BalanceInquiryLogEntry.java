package org.example.transactions.model;
import java.util.Date;
import java.text.SimpleDateFormat;


public class BalanceInquiryLogEntry extends LogEntry {
    private double amount;

    public BalanceInquiryLogEntry(Date timestamp, String user, double amount) {
        super(timestamp, user);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "[" + formatTimestamp(getTimestamp()) + "] " +
                getUser() + " balance inquiry " + amount;
    }
}