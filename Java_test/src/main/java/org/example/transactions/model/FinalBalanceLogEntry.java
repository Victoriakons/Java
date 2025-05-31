package org.example.transactions.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FinalBalanceLogEntry extends LogEntry {
    private double finalBalance;

    public FinalBalanceLogEntry(double finalBalance) {
        super(Calendar.getInstance().getTime(), "");
        this.finalBalance = finalBalance;
    }

    @Override
    public String toString() {
        return "[" + formatCurrentTimeStamp() + "] " +
                "final balance " + finalBalance;
    }

    private static String formatCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(Calendar.getInstance().getTime());
    }
}
