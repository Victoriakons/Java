package org.example.transactions.model;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public abstract class LogEntry implements Comparable<LogEntry> {
    protected Date timestamp;
    protected String user;

    public LogEntry(Date timestamp, String user) {
        this.timestamp = timestamp;
        this.user = user;
    }
    public String getUser() {
        return user;
    }
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(LogEntry other) {
        return timestamp.compareTo(other.timestamp);
    }

    // Общий метод для форматирования даты
    protected String formatTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

}
