import java.util.ArrayList;

public class Student {
    private String name;
    private int id;
    private ArrayList<Double> grades;
    private static double passingGrade = 70.0;

    public Student(String name, int id) {
        this.name = name;
        this.id = id;
        this.grades = new ArrayList<Double>();
    }

    public Student(String name) {
        this(name, 0);
    }

    public void AddGrade(double grade) {
        if (grade >= 0 && grade <= 100) {
            grades.add(grade);
            System.out.println("Added grade: " + grade);
        }
        else {
            System.out.println("Invalid grade");
        }
    }

    public double CalculateAverage() {
        double sum = 0;
        for (double grade : grades) {
            sum += grade;
        }
        return !grades.isEmpty() ? sum / grades.size() : 0.0;
    }

    public double GetHighestGrade() {
        double highest = 0;
        for (double grade : grades) {
            if (grade > highest) {
                highest = grade;
            }
        }
        return highest;
    }

    public double GetLowestGrade() {
        double lowest = 100;
        for (double grade : grades) {
            if (grade < lowest) {
                lowest = grade;
            }
        }
        return lowest;
    }

    public boolean IsPassing() {
        return CalculateAverage() >= passingGrade;
    }

    public static void SetPassingGrade(double newPassingGrade) {
        if (newPassingGrade >= 0 && newPassingGrade <= 100) {
            passingGrade = newPassingGrade;
        }
    }

    public static double GetPassingGrade() {
        return passingGrade;
    }

    @Override
    public String ToString() {
        return "Student Name: " + name + "\nID: " + id + "\nAverage Grade: " + CalculateAverage();
    }
}
