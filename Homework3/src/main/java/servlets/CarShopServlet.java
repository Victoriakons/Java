import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import classes.*;
import management.*;
import logging.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CarShopServlet extends HttpServlet {
    private CarManager carManager = new CarManager();
    private OrderManager orderManager = new OrderManager();
    private UserManager userManager = new UserManager();
    private LogManager logManager = new LogManager();
    private void showRegistrationPage(PrintWriter out) {
        out.println("<h1>Register</h1>");
        out.println("<form method='post' action='?action=register'>");
        out.println("Name: <input type='text' name='name' required><br>");
        out.println("Email: <input type='email' name='email' required><br>");
        out.println("Password: <input type='password' name='password' required><br>");
        out.println("Role: <select name='role'>");
        out.println("<option value='ADMIN'>Admin</option>");
        out.println("<option value='MANAGER'>Manager</option>");
        out.println("<option value='CUSTOMER'>Customer</option>");
        out.println("</select><br>");
        out.println("<input type='submit' value='Register'>");
        out.println("</form>");
    }

    private void showCarManagementPage(PrintWriter out) {
        out.println("<h1>Car Management</h1>");
        out.println("<ul>");
        out.println("<li><a href='?action=addCar'>Add Car</a></li>");
        out.println("<li><a href='?action=updateCar'>Update Car</a></li>");
        out.println("<li><a href='?action=deleteCar'>Delete Car</a></li>");
        out.println("<li><a href='?action=viewCars'>View Cars</a></li>");
        out.println("<li><a href='?action=searchCars'>Search Cars</a></li>");
        out.println("</ul>");
    }

    private void showOrderManagementPage(PrintWriter out) {
        out.println("<h1>Order Management</h1>");
        out.println("<ul>");
        out.println("<li><a href='?action=createOrder'>Create Order</a></li>");
        out.println("<li><a href='?action=updateOrder'>Update Order</a></li>");
        out.println("<li><a href='?action=cancelOrder'>Cancel Order</a></li>");
        out.println("<li><a href='?action=viewOrders'>View Orders</a></li>");
        out.println("<li><a href='?action=searchOrders'>Search Orders</a></li>");
        out.println("</ul>");
    }

    private void showUserManagementPage(PrintWriter out) {
        out.println("<h1>User Management</h1>");
        out.println("<ul>");
        out.println("<li><a href='?action=viewUsers'>View Users</a></li>");
        out.println("<li><a href='?action=updateUser'>Update User</a></li>");
        out.println("<li><a href='?action=deleteUser'>Delete User</a></li>");
        out.println("</ul>");
    }

    private void performRegistration(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String roleStr = request.getParameter("role");
        Role role = Role.valueOf(roleStr.toUpperCase());

        User user = new User(name, email, password, role);
        userManager.registerUser(user); // Assuming userManager handles user registration

        response.sendRedirect("?action=login"); // Redirect to login page after successful registration
    }

    private void addCar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        int year = Integer.parseInt(request.getParameter("year"));
        double price = Double.parseDouble(request.getParameter("price"));
        String condition = request.getParameter("condition");

        Car car = new Car(make, model, year, price, condition);
        carManager.addCar(car); // Assuming carManager handles adding cars

        response.sendRedirect("?action=manageCars"); // Redirect to car management page
    }

    private void updateCar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        int year = Integer.parseInt(request.getParameter("year"));
        double price = Double.parseDouble(request.getParameter("price"));
        String condition = request.getParameter("condition");

        Car updatedCar = new Car(make, model, year, price, condition);
        carManager.updateCar(updatedCar); // Assuming carManager handles updating cars

        response.sendRedirect("?action=manageCars"); // Redirect to car management page
    }

    private void deleteCar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        int year = Integer.parseInt(request.getParameter("year"));

        Car carToDelete = new Car(make, model, year, 0, ""); // Dummy values for price and condition
        carManager.deleteCar(carToDelete); // Assuming carManager handles deleting cars

        response.sendRedirect("?action=manageCars"); // Redirect to car management page
    }

    private void createOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String carMake = request.getParameter("carMake");
        String carModel = request.getParameter("carModel");
        int carYear;

        // Validate and parse the car year
        try {
            carYear = Integer.parseInt(request.getParameter("carYear"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid car year.");
            return;
        }

        // Retrieve the current user from the session
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in.");
            return;
        }

        // Create a new Car object
        Car car = new Car(carMake, carModel, carYear, 0, ""); // Dummy values for price and condition

        // Create a new Order object
        Order order = new Order(car, currentUser);
        orderManager.createOrder(order); // Assuming orderManager handles order creation

        // Log the action
        logManager.logAction("Create Order", currentUser.getEmail(),
                "Order created for car: " + carMake + " " + carModel + " (" + carYear + ")");

        // Redirect to the order management page
        response.sendRedirect("?action=manageOrders");
    }



    private void updateOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String carMake = request.getParameter("carMake");
        String carModel = request.getParameter("carModel");
        int carYear = Integer.parseInt(request.getParameter("carYear"));
        String newStatus = request.getParameter("newStatus");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User customer = userManager.loginUser(email, password);
        if (customer != null) {
            Car car = new Car(carMake, carModel, carYear, 0, ""); // Dummy values for price and condition
            Order updatedOrder = new Order(car, customer);
            updatedOrder.setStatus(newStatus); // Set the new status

            // Call the updateOrder method from OrderManager
            orderManager.updateOrder(updatedOrder);

            // Log the action
            logManager.logAction("Update Order", customer.getEmail(),
                    "Order updated for car: " + carMake + " " + carModel + " (" + carYear + ") with new status: " + newStatus);

            response.sendRedirect("?action=manageOrders"); // Redirect to order management page
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found.");
        }
    }

    private void cancelOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String carMake = request.getParameter("carMake");
        String carModel = request.getParameter("carModel");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User customer = userManager.loginUser(email, password);
        if (customer != null) {
            Car car = new Car(carMake, carModel, 0, 0, ""); // Dummy values for year and condition
            Order orderToCancel = new Order(car, customer);
            orderManager.cancelOrder(orderToCancel); // Assuming cancelOrder sets the status to "Cancelled"

            // Log the action
            logManager.logAction("Cancel Order", customer.getEmail(),
                    "Order cancelled for car: " + carMake + " " + carModel);

            response.sendRedirect("?action=manageOrders"); // Redirect to order management page
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found.");
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String currentPassword = request.getParameter("currentPassword");

        User existingUser = userManager.loginUser(email, currentPassword);
        if (existingUser != null) {
            String newName = request.getParameter("newName");
            String newPassword = request.getParameter("newPassword");

            String updatedName = newName.isEmpty() ? existingUser.getName() : newName;
            String updatedPassword = newPassword.isEmpty() ? existingUser.getPassword() : newPassword;

            User updatedUser = new User(updatedName, email, updatedPassword, existingUser.getRole());
            userManager.registerUser(updatedUser); // Update the user

            // Remove the old user
            userManager.getUsers().remove(existingUser);

            // Log the action
            logManager.logAction("Update User", email, "User updated: " + updatedName);

            response.sendRedirect("?action=manageUsers"); // Redirect to user management page
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found.");
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = userManager.loginUser(email, password);
        if (user != null) {
            userManager.getUsers().remove(user); // Remove the user

            // Log the action
            logManager.logAction("Delete User", email, "User deleted: " + user.getName());

            response.sendRedirect("?action=manageUsers"); // Redirect to user management page
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found or incorrect password.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        if (action == null) {
            action = "home";
        }

        switch (action) {
            case "home":
                showHomePage(out);
                break;
            case "login":
                showLoginPage(out);
                break;
            case "register":
                showRegistrationPage(out);
                break;
            case "manageCars":
                showCarManagementPage(out);
                break;
            case "manageOrders":
                showOrderManagementPage(out);
                break;
            case "manageUsers":
                showUserManagementPage(out);
                break;

            default:
                showErrorPage(out, "Invalid action");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "login":
                performLogin(request, response);
                break;
            case "register":
                performRegistration(request, response);
                break;
            case "addCar":
                addCar(request, response);
                break;
            case "updateCar":
                updateCar(request, response);
                break;
            case "deleteCar":
                deleteCar(request, response);
                break;
            case "createOrder":
                createOrder(request, response);
                break;
            case "updateOrder":
                updateOrder(request, response);
                break;
            case "cancelOrder":
                cancelOrder(request, response);
                break;
            case "updateUser":
                updateUser(request, response);
                break;
            case "deleteUser":
                deleteUser(request, response);
                break;
            default:
                showErrorPage(response.getWriter(), "Invalid action");
        }
    }

    private void showHomePage(PrintWriter out) {
        out.println("<h1>Welcome to Car Shop!</h1>");
        out.println("<a href='?action=login'>Login</a>");
        out.println("<a href='?action=register'>Register</a>");
    }

    private void showLoginPage(PrintWriter out) {
        out.println("<h1>Login</h1>");
        out.println("<form method='post' action='?action=login'>");
        out.println("Email: <input type='text' name='email'><br>");
        out.println("Password: <input type='password' name='password'><br>");
        out.println("<input type='submit' value='Login'>");
        out.println("</form>");
    }

    // Implement other page rendering methods...

    private void performLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = userManager.loginUser(email, password);
        if (user != null) {
            // Set user in session
            request.getSession().setAttribute("user", user);
            response.sendRedirect("?action=home");
        } else {
            response.sendRedirect("?action=login&error=invalidCredentials");
        }
    }


    private void showErrorPage(PrintWriter out, String errorMessage) {
        out.println("<h1>Error</h1>");
        out.println("<p>" + errorMessage + "</p>");
    }
}
