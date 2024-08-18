
package classes;
import java.util.Objects;

public class User {

    private String name;
    private String email;
    private String password;
    private Role role;
    private int purchaseCount;

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.purchaseCount = 0;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public int getPurchaseCount() {
        return purchaseCount;
    }

    public void incrementPurchaseCount() {
        this.purchaseCount++;
    }


    @Override
    public String toString() {
        return String.format("User{name='%s', email='%s', role=%s, purchases=%d}", name, email, role, purchaseCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User user = (User) obj;
        return email.equals(user.email);
    }

    // Override hashCode as well
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
