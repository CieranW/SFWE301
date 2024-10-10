public class Car {
    private String make;
    private String model;
    private boolean isRented;

    public Car(String make, String model) {
        this.make = make;
        this.model = model;
        this.isRented = false;
    }

    public Car() {
        this.make = "NoMake";
        this.model = "NoModel";
        this.isRented = false;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getMake() {
        return this.make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return this.model;
    }

    public boolean getIsRented() {
        return this.isRented;
    }
}
