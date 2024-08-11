package classes;


public class Order {

    private Long id;
    private Car car;
    private User customer;
    private String status;
    private String date;

    public Order(Car car, User customer) {
        this.car = car;
        this.customer = customer;
        this.status = "Pending"; // Default status when creating an order
        this.date = java.time.LocalDate.now().toString(); // Simple date representation
    }



    public Car getCar() {
        return car;
    }

    public User getCustomer() {
        return customer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {

        return date;
    }

    @Override
    public String toString() {
        return String.format("Car: %s, Customer: %s, Status: %s, Date: %s",
                car, customer, status, date);
    }
}