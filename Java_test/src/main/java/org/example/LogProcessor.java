package org.example;

import org.example.transactions.model.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogProcessor {

    // Локальное определение перечисления внутри класса
    private enum OperationType {
        BALANCE_INQUIRY,
        TRANSFER_OUTGOING,
        WITHDRAWAL,
        UNKNOWN
    }

    public static void processLogs(String inputDir, String outputDir) throws IOException {
        Map<String, List<LogEntry>> usersLogs = new HashMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(inputDir))) {
            for (Path path : stream) {
                if (!Files.isRegularFile(path)) continue;

                processFile(usersLogs, path);
            }
        }

        writeUserLogsToFiles(usersLogs, outputDir);
    }

    private static void processFile(Map<String, List<LogEntry>> usersLogs, Path filePath) throws IOException {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]\\s+(\\w+)\\s+(.*)");

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line.trim());
                if (matcher.matches() && matcher.groupCount() >= 3) {
                    String timestampStr = matcher.group(1);
                    String username = matcher.group(2);
                    String operationDetails = matcher.group(3);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date timestamp = sdf.parse(timestampStr);


                    if (operationDetails.startsWith("balance inquiry")) {
                        double amount = extractAmount(operationDetails);
                        addBalanceInquiry(usersLogs, username, timestamp, amount);
                    } else if (operationDetails.startsWith("transferred")) {
                        double amount = extractAmount(operationDetails);
                        String recipient = extractRecipient(operationDetails);
                        addTransferOutgoing(usersLogs, username, timestamp, amount, recipient);
                        addReceivedTransfer(usersLogs, recipient, timestamp, amount, username);
                    } else if (operationDetails.startsWith("withdrew")) {
                        double amount = extractAmount(operationDetails);
                        addWithdrawal(usersLogs, username, timestamp, amount);
                    }
                    System.out.println("Обработана операция: " + line);

                }
            }
        } catch (ParseException e) {
            System.err.println("Ошибка разбора даты: " + e.getMessage());
        }
    }


    private static OperationType determineOperation(String details) {
        if (details.contains("balance inquiry")) return OperationType.BALANCE_INQUIRY;
        else if (details.contains("transferred") || details.contains("to")) return OperationType.TRANSFER_OUTGOING;
        else if (details.contains("withdrew")) return OperationType.WITHDRAWAL;
        return OperationType.UNKNOWN;
    }

    private static double extractAmount(String details) {
        String[] parts = details.split(" ");
        for (String part : parts) {
            try {
                return Double.parseDouble(part);
            } catch (NumberFormatException ignored) {}
        }
        return 0.0; // если ничего не нашли
    }


    private static String extractRecipient(String details) {
        // ищем "to userX"
        String[] parts = details.split(" ");
        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].equals("to")) {
                return parts[i + 1];
            }
        }
        return "UNKNOWN";
    }

    private static void addBalanceInquiry(Map<String, List<LogEntry>> logs, String user,
                                          Date timestamp, double amount) {
        logs.computeIfAbsent(user, k -> new ArrayList<>())
                .add(new BalanceInquiryLogEntry(timestamp, user, amount));
    }

    private static void addTransferOutgoing(Map<String, List<LogEntry>> logs, String sender,
                                            Date timestamp, double amount, String recipient) {
        logs.computeIfAbsent(sender, k -> new ArrayList<>())
                .add(new TransferOutgoingLogEntry(timestamp, sender, amount, recipient));
    }

    private static void addReceivedTransfer(Map<String, List<LogEntry>> logs, String receiver,
                                            Date timestamp, double amount, String sender) {
        logs.computeIfAbsent(receiver, k -> new ArrayList<>())
                .add(new ReceivedTransferLogEntry(timestamp, receiver, amount, sender));
    }

    private static void addWithdrawal(Map<String, List<LogEntry>> logs, String user,
                                      Date timestamp, double amount) {
        logs.computeIfAbsent(user, k -> new ArrayList<>())
                .add(new WithdrawalLogEntry(timestamp, user, amount));
    }

    private static void writeUserLogsToFiles(Map<String, List<LogEntry>> usersLogs, String outputDir) throws IOException {
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            boolean created = outputDirFile.mkdir();
            if (!created) throw new IOException("Невозможно создать директорию " + outputDir);
        }

        for (String user : usersLogs.keySet()) {
            List<LogEntry> entries = usersLogs.get(user);
            Collections.sort(entries, Comparator.comparing(LogEntry::getTimestamp));

            calculateFinalBalanceAndWrite(entries, user, outputDir);
        }
    }

    private static void calculateFinalBalanceAndWrite(List<LogEntry> entries, String user, String outputDir) throws IOException {
        double currentBalance = 0.0;
        for (LogEntry entry : entries) {
            if (entry instanceof BalanceInquiryLogEntry) {
                currentBalance = ((BalanceInquiryLogEntry) entry).getAmount();
            } else if (entry instanceof TransferOutgoingLogEntry) {
                currentBalance -= ((TransferOutgoingLogEntry) entry).getAmount();
            } else if (entry instanceof ReceivedTransferLogEntry) {
                currentBalance += ((ReceivedTransferLogEntry) entry).getAmount();
            } else if (entry instanceof WithdrawalLogEntry) {
                currentBalance -= ((WithdrawalLogEntry) entry).getAmount();
            }
        }

        // Финальная запись с актуальным балансом
        entries.add(new FinalBalanceLogEntry(currentBalance));

        String filename = user + ".log";
        Path outputFile = Paths.get(outputDir, filename);
        BufferedWriter writer = Files.newBufferedWriter(outputFile);

        try {
            for (LogEntry entry : entries) {
                writer.write(entry.toString());
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    private static String formatTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    private static String formatCurrentTimeStamp() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }
}