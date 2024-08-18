package management;

import java.util.List;
import java.util.ArrayList;
import classes.*;

public class CarManager {
    private List<Car> cars = new ArrayList<>(); //хранение данных о машине в масиве

    public void addCar(Car car) {
        cars.add(car);
    }

    public void updateCar(Car updatedCar) {
        for (int i = 0; i < cars.size(); i++) {
            Car existingCar = cars.get(i); //найти машину на определенном индесе (у нас массив данных, у каждого элемента свой индекс)
            if (existingCar.getMake().equals(updatedCar.getMake()) &&
                    existingCar.getModel().equals(updatedCar.getModel()) &&
                    existingCar.getYear() == updatedCar.getYear()) {
                // Если все сходится - обновляем данные нужной машины
                existingCar.setPrice(updatedCar.getPrice());
                existingCar.setCondition(updatedCar.getCondition());
                System.out.println("Car updated successfully.");
                return; // выход
            }
        }
        System.out.println("Car not found.");
    }

    public void deleteCar(Car car) {
        for (int i = 0; i < cars.size(); i++) {
            Car existingCar = cars.get(i);
            if (existingCar.getMake().equals(car.getMake()) &&
                    existingCar.getModel().equals(car.getModel()) &&
                    existingCar.getYear() == car.getYear()) {
                cars.remove(existingCar);
                System.out.println("Car deleted successfully.");
                return;
            }
        }
    }

    public List<Car> getCars() {
        return cars;
    }
}