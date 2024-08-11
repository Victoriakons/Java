
package management;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classes.*;

public class UserManager {
    private Map<String, User> users = new HashMap<>();

    public void registerUser(User user) {
        users.put(user.getEmail(), user);
    }

    public User loginUser(String email, String password) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}