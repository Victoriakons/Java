package org.example.transactions.model;
import java.util.Date;

public class ReceivedTransferLogEntry extends LogEntry {
    private double amount;
    private String sender;

    public ReceivedTransferLogEntry(Date timestamp, String user, double amount, String sender) {
        super(timestamp, user);
        this.amount = amount;
        this.sender = sender;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "[" + formatTimestamp(getTimestamp()) + "] " +
                getUser() + " received " + amount + " from " + sender;
    }
}