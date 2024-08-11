
package logging;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
public class CheckLogs {
    private LogManager logManager;

    public CheckLogs(LogManager logManager) {
        this.logManager = logManager;
    }

    public List<LogActions> filterLogs(String user, String type) {
        return logManager.filterLogs(user, type);
    }

    public void printLogs() {
        for (LogActions log : logManager.getLogs()) {
            System.out.println(log);
        }
    }

    public void exportLogsToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (LogActions log : logManager.getLogs()) {
                writer.write(log.toString());
                writer.newLine();
            }
            System.out.println("Logs exported successfully to " + filename);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}