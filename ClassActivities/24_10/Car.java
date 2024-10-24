public class Car extends Vehicle {
    private int numDoors;
    private String color;

    public Car(String make, String model, int year, int doors, String color) {
        super(make, model, year);
        this.numDoors = doors;
        this.color = color;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Doors: " + numDoors);
        System.out.println("Color: " + color);
    }

    public int getDoors() {
        return numDoors;
    }

    public String getColor() {
        return color;
    }

    public void setDoors(int doors) {
        this.numDoors = doors;
    }

    public void setColor(String color) {
        this.color = color;
    }
    
}
