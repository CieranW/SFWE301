public class CarRentalSystem {
    public static void main(String[] args) {
        Car car1 = new Car("Toyota", "Corolla");
        Car car2 = new Car("Honda", "Civic");

        car2.rentCar();
        car2.rentCar();

        car1.rentCar();
        car1.returnCar();

        car1.returnCar();
        car2.returnCar();
    }
}
