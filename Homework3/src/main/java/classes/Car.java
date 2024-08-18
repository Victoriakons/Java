
package classes;
import java.util.Objects;


public class Car {

    private String make;//марка
    private String model;
    private int year;
    private double price;
    private String condition;

    public Car(String make, String model, int year, double price, String condition) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.condition = condition;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    public String getCondition() {
        return condition;
    }

    public void setPrice(double price) {
        if (price >= 0) { // Optional: Check for valid price
            this.price = price;
        } else {
            System.out.println("Price cannot be negative.");
        }
    }

    public void setCondition(String condition) {
        if (condition != null && !condition.isEmpty()) {
            this.condition = condition;
        } else {
            System.out.println("Condition cannot be null or empty.");
        }
    }

    @Override
    public String toString() {
        return String.format("Make: %s, Model: %s, Year: %d, Price: $%.2f, Condition: %s",
                make, model, year, price, condition);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Car)) return false;
        Car car = (Car) obj;
        return year == car.year &&
                make.equals(car.make) &&
                model.equals(car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(make, model, year);
    }
}