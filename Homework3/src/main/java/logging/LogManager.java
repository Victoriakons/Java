package logging;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
public class LogManager {
    private List<LogActions> logs = new ArrayList<>();

    public void logAction(String action, String user, String details) {
        logs.add(new LogActions(action, user, details));
    }


    public List<LogActions> getLogs() {
        return logs;
    }

    public List<LogActions> filterLogs(String user, String type) {
        List<LogActions> filteredLogs = new ArrayList<>();
        for (LogActions log : logs) {
            if ((user.isEmpty() || log.getUser().equals(user)) &&
                    (type.isEmpty() || log.getAction().equals(type))) {
                filteredLogs.add(log);
            }
        }
        return filteredLogs;
    }
}
