import java.util.Scanner;

public class main {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int studentCount = scanner.nextInt();

        Student[] students = new Student[studentCount];

        for (int i = 0; i < studentCount; i++) {
            String name = scanner.next();
            int id = scanner.nextInt();
            students[i] = new Student(name, id);
        }
    }
}
