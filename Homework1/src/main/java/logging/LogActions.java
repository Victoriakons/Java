
package logging;
import java.time.LocalDateTime;
public class LogActions {
    private String action;
    private String user;
    private LocalDateTime timestamp;
    private String details;

    public LogActions(String action, String user, String details) {
        this.action = action;
        this.user = user;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }
    public String getUser() {
        return user;
    }
    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return String.format("%s | User: %s | Action: %s | Details: %s",
                timestamp, user, action, details);
    }
}