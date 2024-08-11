package io.ylab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PreparedStatementExample {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER_NAME = "postgres";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            createTables(connection);
            insertUsers(connection);
            insertCars(connection);
            insertOrders(connection);
            ResultSet resultSet = retrieveUsers(connection);
            printUsers(resultSet);
            resultSet = retrieveCars(connection);
            printCars(resultSet);
            resultSet = retrieveOrders(connection);
            printOrders(resultSet);
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY," +
                "name VARCHAR(255)," +
                "email VARCHAR(255) UNIQUE," +
                "password VARCHAR(255)," +
                "role VARCHAR(50)," +
                "purchase_count INT DEFAULT 0)";
        String createCarsTableSQL = "CREATE TABLE IF NOT EXISTS cars (" +
                "id SERIAL PRIMARY KEY," +
                "make VARCHAR(255)," +
                "model VARCHAR(255)," +
                "year INT," +
                "price DECIMAL(10,2)," +
                "condition VARCHAR(50))";
        String createOrdersTableSQL = "CREATE TABLE IF NOT EXISTS orders (" +
                "id SERIAL PRIMARY KEY," +
                "car_id BIGINT REFERENCES cars(id)," +
                "customer_id BIGINT REFERENCES users(id)," +
                "status VARCHAR(50) DEFAULT 'Pending'," +
                "order_date DATE)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createUsersTableSQL);
            statement.execute(createCarsTableSQL);
            statement.execute(createOrdersTableSQL);
        }
    }

    private static void insertUsers(Connection connection) throws SQLException {
        String insertUserSQL = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {
            preparedStatement.setString(1, "Admin");
            preparedStatement.setString(2, "admin@example.com");
            preparedStatement.setString(3, "adminpass");
            preparedStatement.setString(4, "ADMIN");
            preparedStatement.executeUpdate();
        }
    }

    private static void insertCars(Connection connection) throws SQLException {
        String insertCarSQL = "INSERT INTO cars (make, model, year, price, condition) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCarSQL)) {
            preparedStatement.setString(1, "Toyota");
            preparedStatement.setString(2, "Camry");
            preparedStatement.setInt(3, 2020);
            preparedStatement.setDouble(4, 24000.00);
            preparedStatement.setString(5, "New");
            preparedStatement.executeUpdate();
        }
    }

    private static void insertOrders(Connection connection) throws SQLException {
        String insertOrderSQL = "INSERT INTO orders (car_id, customer_id, status, order_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrderSQL)) {
            preparedStatement.setLong(1, 1); // Assuming car_id 1 exists
            preparedStatement.setLong(2, 1); // Assuming customer_id 1 exists
            preparedStatement.setString(3, "Pending");
            preparedStatement.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
            preparedStatement.executeUpdate();
        }
    }

    private static ResultSet retrieveUsers(Connection connection) throws SQLException {
        String retrieveUsersSQL = "SELECT * FROM users";
        Statement statement = connection.createStatement();
        return statement.executeQuery(retrieveUsersSQL);
    }

    private static ResultSet retrieveCars(Connection connection) throws SQLException {
        String retrieveCarsSQL = "SELECT * FROM cars";
        Statement statement = connection.createStatement();
        return statement.executeQuery(retrieveCarsSQL);
    }

    private static ResultSet retrieveOrders(Connection connection) throws SQLException {
        String retrieveOrdersSQL = "SELECT * FROM orders";
        Statement statement = connection.createStatement();
        return statement.executeQuery(retrieveOrdersSQL);
    }

    private static void printUsers(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String email = resultSet.getString("email");
            System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email);
        }
    }

    private static void printCars(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String make = resultSet.getString("make");
            String model = resultSet.getString("model");
            int year = resultSet.getInt("year");
            double price = resultSet.getDouble("price");
            System.out.println("ID: " + id + ", Make: " + make + ", Model: " + model + ", Year: " + year + ", Price: " + price);
        }
    }

    private static void printOrders(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            long carId = resultSet.getLong("car_id");
            long customerId = resultSet.getLong("customer_id");
            String status = resultSet.getString("status");
            System.out.println("ID: " + id + ", Car ID: " + carId + ", Customer ID: " + customerId + ", Status: " + status);
        }
    }
}
