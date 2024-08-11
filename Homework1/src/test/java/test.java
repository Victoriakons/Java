import classes.*;
import management.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
class CarManagerTest {
    private CarManager carManager;

    @BeforeEach
    void setUp() {
        carManager = new CarManager();
    }

    @Test
    void testAddCar() {
        Car car = new Car("Toyota", "Camry", 2020, 25000.0, "Excellent");
        carManager.addCar(car);

        List<Car> cars = carManager.getCars();
        assertFalse(cars.isEmpty());
        assertTrue(cars.contains(car));
    }

    @Test
    void testUpdateCar() {
        Car car = new Car("Toyota", "Camry", 2020, 25000.0, "Excellent");
        carManager.addCar(car);

        Car updatedCar = new Car("Toyota", "Camry", 2020, 30000.0, "Good");
        carManager.updateCar(updatedCar);

        List<Car> cars = carManager.getCars();
        assertEquals(1, cars.size());
        Car managedCar = cars.get(0);
        assertEquals(30000.0, managedCar.getPrice());
        assertEquals("Good", managedCar.getCondition());
    }

    @Test
    void testDeleteCar() {
        Car car = new Car("Toyota", "Camry", 2020, 25000.0, "Excellent");
        carManager.addCar(car);

        carManager.deleteCar(car);

        List<Car> cars = carManager.getCars();
        assertTrue(cars.isEmpty());
    }

    @Test
    void testGetCars() {
        Car car1 = new Car("Toyota", "Camry", 2020, 25000.0, "Excellent");
        Car car2 = new Car("Honda", "Civic", 2018, 20000.0, "Good");
        carManager.addCar(car1);
        carManager.addCar(car2);

        List<Car> cars = carManager.getCars();
        assertEquals(2, cars.size());
        assertTrue(cars.contains(car1));
        assertTrue(cars.contains(car2));
    }
}

class OrderManagerTest {
    private OrderManager orderManager;

    @BeforeEach
    void setUp() {
        orderManager = new OrderManager();
    }

    @Test
    void testCreateOrder() {
        Car car = new Car("Toyota", "Camry", 2020, 25000.0, "Excellent");
        User user = new User("John Doe", "John@mail.ru","1234",Role.MANAGER);
        Order order = new Order(car,user);
        orderManager.createOrder(order);

        List<Order> orders = orderManager.getOrders();
        assertEquals(1, orders.size(), "Order list should contain one order.");
        assertTrue(orders.contains(order), "Order list should contain the created order.");
    }

    @Test
    void testUpdateOrder() {
        Car car = new Car("Toyota", "Camry", 2020, 25000.0, "Excellent");
        User user = new User("Bob Doe", "Bob@mail.ru","1234",Role.MANAGER);
        Order order = new Order(car, user);
        orderManager.createOrder(order);

        //обновляем статус заказа
        order.setStatus("Completed");

        orderManager.updateOrder(order);

        List<Order> orders = orderManager.getOrders();
        assertEquals(1, orders.size(), "Order list should still contain one order.");
        assertEquals("Completed", orders.get(0).getStatus(), "Order status should be updated to 'Completed'.");
    }

    @Test
    void testCancelOrder() {
        Car car = new Car("Toyota", "Camry", 2020, 25000.0, "Excellent");
        User user = new User("John Doe", "John@mail.ru","1234",Role.MANAGER);
        Order order = new Order(car, user);
        orderManager.createOrder(order);

        orderManager.cancelOrder(order);

        List<Order> orders = orderManager.getOrders();
        assertEquals(1, orders.size(), "Order list should still contain one order.");
        assertEquals("Cancelled", orders.get(0).getStatus(), "Order status should be 'Cancelled'.");
    }

    @Test
    void testGetOrders() {
        Car car1 = new Car("Toyota", "Camry", 2020, 25000.0, "Excellent");
        User user1 = new User("John Doe", "John@mail.ru","1234",Role.MANAGER);;
        Order order1 = new Order(car1, user1);
        orderManager.createOrder(order1);

        Car car2 = new Car("Honda", "Civic", 2018, 20000.0, "Good");
        User user2 = new User("Bob Doe", "Bob@mail.ru","1234",Role.MANAGER);;
        Order order2 = new Order(car2, user2);
        orderManager.createOrder(order2);

        List<Order> orders = orderManager.getOrders();
        assertEquals(2, orders.size(), "Order list should contain two orders.");
        assertTrue(orders.contains(order1), "Order list should contain the first order.");
        assertTrue(orders.contains(order2), "Order list should contain the second order.");
    }

    @Test
    void testUpdateOrder_NotFound() {
        Car car = new Car("Toyota", "Camry", 2020, 25000.0, "Excellent");
        User user = new User("John Doe", "John@mail.ru","1234",Role.MANAGER);;
        Order order = new Order(car, user);
        orderManager.createOrder(order);

        Car nonExistentCar = new Car("Ford", "Focus", 2019, 22000.0, "Good");
        User nonExistentUser = new User("Bob Doe", "Bob@mail.ru","1234",Role.MANAGER);;
        Order updatedOrder = new Order(nonExistentCar, nonExistentUser);
        orderManager.updateOrder(updatedOrder);

        List<Order> orders = orderManager.getOrders();
        assertEquals(1, orders.size(), "Order list should still contain one order.");
        assertEquals("Pending", orders.get(0).getStatus(), "Order status should not change when updating a non-existent order.");
    }
}

class UserManagerTest {
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
    }

    @Test
    void testRegisterUser() {
        User user = new User("John Doe", "john@example.com", "password", Role.CUSTOMER);
        userManager.registerUser(user);

        List<User> users = userManager.getUsers();
        assertTrue(users.contains(user), "User list should contain the registered user.");
    }

    @Test
    void testLoginUser_ValidCredentials() {
        User user = new User("John Doe", "john@example.com", "password", Role.CUSTOMER);
        userManager.registerUser(user);

        User loggedInUser = userManager.loginUser("john@example.com", "password");
        assertNotNull(loggedInUser, "User should be logged in successfully.");
        assertEquals(user, loggedInUser, "Logged in user should match the registered user.");
    }

    @Test
    void testLoginUser_InvalidPassword() {
        User user = new User("John Doe", "john@example.com", "password", Role.CUSTOMER);
        userManager.registerUser(user);

        User loggedInUser = userManager.loginUser("john@example.com", "wrongPassword");
        assertNull(loggedInUser, "Login should fail with invalid password.");
    }

    @Test
    void testLoginUser_UserNotFound() {
        User user = new User("John Doe", "john@example.com", "password", Role.CUSTOMER);
        userManager.registerUser(user);

        User loggedInUser = userManager.loginUser("nonexistent@example.com", "password");
        assertNull(loggedInUser, "Login should fail for non-existent user.");
    }

    @Test
    void testGetUsers() {
        User user1 = new User("John Doe", "john@example.com", "password", Role.CUSTOMER);
        User user2 = new User("Jane Doe", "jane@example.com", "password", Role.CUSTOMER);
        userManager.registerUser(user1);
        userManager.registerUser(user2);

        List<User> users = userManager.getUsers();
        assertEquals(2, users.size(), "User list should contain two users.");
        assertTrue(users.contains(user1), "User list should contain the first user.");
        assertTrue(users.contains(user2), "User list should contain the second user.");
    }
}