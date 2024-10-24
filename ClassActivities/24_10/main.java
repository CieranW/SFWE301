public class main {
    public static void main(String[] args) {
        Car car = new Car("Toyota", "Corolla", 2020, 4, "Red");
        Truck truck = new Truck("Ford", "F-150", 2021, 2000);

        System.out.println("Car Info: ");
        car.displayInfo();
        System.out.println("\nTruck Info: ");
        truck.displayInfo();
    }
}
