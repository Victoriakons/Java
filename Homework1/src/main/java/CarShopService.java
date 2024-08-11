import classes.*;
import management.*;
import logging.*;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

// Основной класс приложения
public class CarShopService {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CarManager carManager = new CarManager();
    private static final OrderManager orderManager = new OrderManager();
    private static final UserManager userManager = new UserManager();
    private static User currentUser;

    private static LogManager logManager = new LogManager();

    public static void viewLogs() {
        CheckLogs checkLogs = new CheckLogs(logManager);
        checkLogs.printLogs(); // Вызов метода на экземпляре checkLogs
    }

    public static void exportLogs() {
        System.out.print("Enter filename to export logs: ");
        String filename = scanner.nextLine();
        CheckLogs checkLogs = new CheckLogs(logManager);
        checkLogs.exportLogsToFile(filename); // Вызов метода на экземпляре checkLogs
    }

    private static void filterLogs() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user (leave blank for all): ");
        String user = scanner.nextLine();
        System.out.print("Enter action type (leave blank for all): ");
        String actionType = scanner.nextLine();

        CheckLogs checkLogs = new CheckLogs(logManager);
        List<LogActions> filteredLogs = checkLogs.filterLogs(user, actionType);

        if (filteredLogs.isEmpty()) {
            System.out.println("No logs found matching the criteria.");
        } else {
            System.out.println("Filtered Logs:");
            for (LogActions log : filteredLogs) {
                System.out.println(log);
            }
        }
    }

    private static void manageOrders() {
        System.out.println("1. Create Order\n2. Update Order\n3. Cancel Order\n4. View Orders\n5. Search Orders");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                createOrder();
                break;
            case 2:
                updateOrder();
                break;
            case 3:
                cancelOrder();
                break;
            case 4:
                viewOrders();
                break;
            case 5:
                searchOrders();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void searchOrders() {
        System.out.println("Enter search criteria (leave blank to skip):");

        System.out.print("Customer Email: ");
        String customerEmail = scanner.nextLine();

        System.out.print("Order Status: ");
        String status = scanner.nextLine();

        System.out.print("Car Make: ");
        String carMake = scanner.nextLine();

        System.out.print("Car Model: ");
        String carModel = scanner.nextLine();

        System.out.print("Order Date (YYYY-MM-DD, leave blank for no filter): ");
        String orderDate = scanner.nextLine();

        List<Order> filteredOrders = orderManager.getOrders().stream()
                .filter(order -> (customerEmail.isEmpty() || order.getCustomer().getEmail().equalsIgnoreCase(customerEmail)) &&
                        (status.isEmpty() || order.getStatus().equalsIgnoreCase(status)) &&
                        (carMake.isEmpty() || order.getCar().getMake().equalsIgnoreCase(carMake)) &&
                        (carModel.isEmpty() || order.getCar().getModel().equalsIgnoreCase(carModel)) &&
                        (orderDate.isEmpty() || order.getOrderDate().equals(orderDate)))
                .collect(Collectors.toList());

        if (filteredOrders.isEmpty()) {
            System.out.println("No orders found matching the criteria.");
        } else {
            System.out.println("Filtered Orders:");
            for (Order order : filteredOrders) {
                System.out.println(order);
            }
        }
    }


    private static void createOrder() {
        System.out.print("Enter car make: ");
        String make = scanner.nextLine();
        System.out.print("Enter car model: ");
        String model = scanner.nextLine();
        System.out.print("Enter car year: ");
        int year = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (currentUser == null) {
            System.out.println("User not found");
            return;
        }

        Car car = new Car(make, model, year, 0, ""); // дефолтное значение
        Order order = new Order(car, currentUser);
        orderManager.createOrder(order);

        // Логируем действие
        logManager.logAction("Create Order", currentUser.getEmail(), "Order created for car: " + make + " " + model + " (" + year + ")");

        System.out.println("Order logged successfully.");
    }

    private static void updateOrder() {
        System.out.print("Enter car make to update the order: ");
        String make = scanner.nextLine();
        System.out.print("Enter car model to update the order: ");
        String model = scanner.nextLine();
        System.out.print("Enter car year to update the order: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter new status for the order: ");
        String newStatus = scanner.nextLine();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine(); // Prompt for password

        User customer = userManager.loginUser(email, password); // Use the provided password
        if (customer != null) {
            // Create a dummy order to find the existing one
            Car car = new Car(make, model, year, 0, "");
            Order updatedOrder = new Order(car, customer);
            updatedOrder.setStatus(newStatus); // Set the new status

            // Print existing orders for debugging
            List<Order> orders = orderManager.getOrders();
            System.out.println("Existing Orders:");
            for (Order order : orders) {
                System.out.println(order);
            }

            // Call the updateOrder method from OrderManager
            orderManager.updateOrder(updatedOrder);
            // Логируем действие
            logManager.logAction("Update Order", customer.getEmail(), "Order updated for car: " + make + " " + model + " (" + year + ") with new status: " + newStatus);

            System.out.println("Order logged successfully.");
        } else {
            System.out.println("User not found.");
        }

    }

    private static void cancelOrder() {
        System.out.print("Enter car make to cancel the order: ");
        String make = scanner.nextLine();
        System.out.print("Enter car model to cancel the order: ");
        String model = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        User customer = userManager.loginUser(email, password); // тут вводим свой пароль
        if (customer != null) {
            Car car = new Car(make, model, 0, 0, "");
            Order orderToCancel = new Order(car, customer);

            orderManager.cancelOrder(orderToCancel);
            // Логируем действие
            logManager.logAction("Cancel Order", customer.getEmail(), "Order cancelled for car: " + make + " " + model);
            System.out.println("Order cancelled successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    private static void viewOrders() {
        List<Order> orders = orderManager.getOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
        } else {
            for (Order order : orders) {
                System.out.println(order);
            }
        }
    }
    private static void manageUsers() {
        System.out.println("1. View Users\n2. Update User\n3. Delete User");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                viewUsers();
                break;
            case 2:
                updateUser();
                break;
            case 3:
                deleteUser();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void viewUsers() {
        List<User> users = userManager.getUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    private static void updateUser() {
        System.out.print("Enter the email of the user to update: ");
        String email = scanner.nextLine();

        System.out.print("Enter your current password: ");
        String currentPassword = scanner.nextLine();

        // проверка на существование юзера
        User existingUser = userManager.loginUser(email, currentPassword);
        if (existingUser != null) {
            System.out.print("Enter new name (leave blank to keep current): ");
            String newName = scanner.nextLine();
            System.out.print("Enter new password (leave blank to keep current): ");
            String newPassword = scanner.nextLine();

            String updatedName = newName.isEmpty() ? existingUser.getName() : newName;
            String updatedPassword = newPassword.isEmpty() ? existingUser.getPassword() : newPassword;

            User updatedUser = new User(updatedName, email, updatedPassword, existingUser.getRole()); //роль без изменений

            // Регистрация обновленной информации для юзера
            userManager.registerUser(updatedUser);

            // Удаляем старого юзера
            userManager.getUsers().remove(existingUser);
            // Логируем действие
            logManager.logAction("Update User", email, "User updated: " + updatedName);
            System.out.println("User updated successfully.");
        } else {
            System.out.println("User not found.");
        }
    }


    private static void deleteUser() {
        System.out.print("Enter the email of the user to delete: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        User user = userManager.loginUser(email, password);
        if (user != null) {
            userManager.getUsers().remove(user);
            // Логируем действие
            logManager.logAction("Delete User", email, "User deleted: " + user.getName());
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("User not found or incorrect password.");
        }
    }

    private static void updateCar() {
        System.out.print("Enter the make of the car to update: ");
        String make = scanner.nextLine();
        System.out.print("Enter the model of the car to update: ");
        String model = scanner.nextLine();
        System.out.print("Enter the year of the car to update: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        // создаем объект машины с новыми данными
        System.out.print("Enter new price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter new condition: ");
        String condition = scanner.nextLine();

        Car updatedCar = new Car(make, model, year, price, condition);

        carManager.updateCar(updatedCar);

        // Логируем действие
        logManager.logAction("Update Car", currentUser.getEmail(), "Car updated: " + make + " " + model + " (" + year + "), new price: " + price + ", new condition: " + condition);

        System.out.println("Car updated successfully.");
    }


    private static void viewCars() {
        List<Car> cars = carManager.getCars();
        if (cars.isEmpty()) {
            System.out.println("No cars found.");
        } else {
            for (Car car : cars) {

                System.out.println(car);
            }
        }
    }
    private static void searchCars() {
        System.out.println("Enter search criteria (leave blank to skip):");

        System.out.print("Make: ");
        String make = scanner.nextLine();

        System.out.print("Model: ");
        String model = scanner.nextLine();

        System.out.print("Year (0 for no filter): ");
        int year = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Max Price (0 for no filter): ");
        double maxPrice = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Condition: ");
        String condition = scanner.nextLine();

        List<Car> filteredCars = carManager.getCars().stream()
                .filter(car -> (make.isEmpty() || car.getMake().equalsIgnoreCase(make)) &&
                        (model.isEmpty() || car.getModel().equalsIgnoreCase(model)) &&
                        (year == 0 || car.getYear() == year) &&
                        (maxPrice == 0 || car.getPrice() <= maxPrice) &&
                        (condition.isEmpty() || car.getCondition().equalsIgnoreCase(condition)))
                .collect(Collectors.toList());

        if (filteredCars.isEmpty()) {
            System.out.println("No cars found matching the criteria.");
        } else {
            System.out.println("Filtered Cars:");
            for (Car car : filteredCars) {
                System.out.println(car);
            }
        }
    }

    public static void main(String[] args) {
        // Регистрируем тестовых юзеров
        User admin = new User("Admin", "admin@example.com", "password", Role.ADMIN);
        userManager.registerUser(admin);
        User manager = new User("Manager", "manager@example.com", "password", Role.MANAGER);
        userManager.registerUser(manager);
        User customer = new User("Customer", "customer@example.com", "password", Role.CUSTOMER);
        userManager.registerUser(customer);

        // Основной луп программы
        while (true) {
            if (currentUser == null) {
                System.out.println("1. Login\n2. Register\n3. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        login();
                        break;
                    case 2:
                        register();
                        break;
                    case 3:
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice.");
                }
            } else {
//                    System.out.println("Welcome, " + currentUser.getName() + "!");
                System.out.println("1. Manage Cars\n2. Manage Orders\n3. Manage Users\n4. View Logs\n5. Export Logs\n6. Filter Logs\n7. Logout");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        manageCars();
                        break;
                    case 2:
                        manageOrders();
                        break;
                    case 3:
                        if (currentUser.getRole() == Role.ADMIN) {
                            manageUsers();
                        } else {
                            System.out.println("You don't have permission to manage users.");
                        }
                        break;
                    case 4:
                        viewLogs(); // Добавляем возможность просмотра логов
                        break;
                    case 5:
                        exportLogs(); // Добавляем возможность экспорта логов
                        break;
                    case 6:
                        filterLogs(); // Добавляем возможность фильтрации логов
                        break;
                    case 7:
                        currentUser = null; // Logout
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }
    }

    private static void login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        currentUser = userManager.loginUser(email, password);
        if (currentUser == null) {
            System.out.println("Invalid email or password.");
            // Логируем неудачный вход
            logManager.logAction("Login Attempt", email, "Failed login attempt.");
        } else {
            // Логируем успешный вход
            logManager.logAction("Login", email, "User logged in successfully.");
        }
    }

    private static void register() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Role (ADMIN, MANAGER, CUSTOMER): ");
        Role role = Role.valueOf(scanner.nextLine().toUpperCase());
        User user = new User(name, email, password, role);
        userManager.registerUser(user);
        // Логируем действие
        logManager.logAction("Register User", email, "User registered: " + name + " with role: " + role);
        System.out.println("User registered successfully.");
    }

    private static void manageCars() {
        System.out.println("1. Add Car\n2. Update Car\n3. Delete Car\n4. View Cars\n5. Search Cars");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                addCar();
                break;
            case 2:
                updateCar();
                break;
            case 3:
                deleteCar();
                break;
            case 4:
                viewCars();
                break;
            case 5:
                searchCars();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void addCar() {
        System.out.print("Make: ");
        String make = scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();
        System.out.print("Year: ");
        int year = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Condition: ");
        String condition = scanner.nextLine();
        Car car = new Car(make, model, year, price, condition);
        carManager.addCar(car);
        // Логируем действие
        logManager.logAction("Add Car", currentUser.getEmail(), "Car added: " + make + " " + model + " (" + year + "), Price: " + price + ", Condition: " + condition);
        System.out.println("Car added successfully.");
    }

    private static void deleteCar() {
        System.out.print("Enter the make of the car to delete: ");
        String make = scanner.nextLine();
        System.out.print("Enter the model of the car to delete: ");
        String model = scanner.nextLine();
        System.out.print("Enter the year of the car to delete: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        // Создаем объект машины и инициализируем
        Car carToDelete = new Car(make, model, year, 0, ""); // Price and condition can be dummy values

        // Логируем действие
        logManager.logAction("Delete Car", currentUser.getEmail(), "Car deleted: " + make + " " + model + " (" + year + ")");

        carManager.deleteCar(carToDelete);
    }

}