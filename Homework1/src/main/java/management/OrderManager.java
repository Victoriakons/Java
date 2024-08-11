package management;

import java.util.ArrayList; // Импортируйте ArrayList
import java.util.List;      // Импортируйте List
import classes.*;


public class OrderManager {
    private List<Order> orders = new ArrayList<>();

    public void createOrder(Order order) {
        orders.add(order);
        System.out.println("Order created successfully.");
    }

    public void updateOrder(Order updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            Order existingOrder = orders.get(i);

            // Сходится ли инфа о машине и клиенте (проверка) и устанавливаем новый статус
            if (existingOrder.getCar().equals(updatedOrder.getCar()) &&
                    existingOrder.getCustomer().equals(updatedOrder.getCustomer())) {
                existingOrder.setStatus(updatedOrder.getStatus());
                System.out.println("Order updated successfully.");
                return;
            }
        }
        System.out.println("Order not found.");
    }


    public void cancelOrder(Order order) {
        order.setStatus("Cancelled");
    }

    public List<Order> getOrders() {
        return orders;
    }
}