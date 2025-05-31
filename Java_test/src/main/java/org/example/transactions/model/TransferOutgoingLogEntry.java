package org.example.transactions.model;
import java.util.Date;

public class TransferOutgoingLogEntry extends LogEntry {
    private double amount;
    private String recipient;

    public TransferOutgoingLogEntry(Date timestamp, String user, double amount, String recipient) {
        super(timestamp, user);
        this.amount = amount;
        this.recipient = recipient;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "[" + formatTimestamp(getTimestamp()) + "] " +
                getUser() + " transferred " + amount + " to " + recipient;
    }
}