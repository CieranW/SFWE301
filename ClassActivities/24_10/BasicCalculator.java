import java.util.InputMismatchException;
import java.util.Scanner;

public class BasicCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        do {
            try {
                System.out.println("Enter the first number: ");
                double firstNumber = scanner.nextDouble();

                System.out.println("Enter the second number: ");
                double secondNumber = scanner.nextDouble();

                System.out.println("Enter the operation: ");
                String operation = scanner.next();

                double result = 0;
            switch (operation) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "-":
                    result = firstNumber - secondNumber;
                    break;
                case "*":
                    result = firstNumber * secondNumber;
                    break;
                case "/":
                    if (secondNumber == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    result = firstNumber / secondNumber;
                    break;
                case "^":
                    result = Math.pow(firstNumber, secondNumber);
                    break;
                default:
                    System.out.println("Invalid operation");
                    break;
                }
                System.out.println("Result: " + result);
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid input");
                scanner.nextLine();
            }
            catch (ArithmeticException e) {
                System.out.println(e.getMessage());
            } 

            System.out.println("Perform another operation? (y/n)");
            String response = scanner.next();
            if (response.equals("n")) {
                break;
            }
        } while (true);

        scanner.close();
    }
}
