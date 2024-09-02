import java.util.Scanner;

public class main{
    public static void main(String[] args){
        float num1, num2;

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the first number:");
        num1 = sc.nextFloat();
        System.out.println("Enter the second number:");
        num2 = sc.nextFloat();

        float add = num1 + num2;
        float sub = num1 - num2;
        float mul = num1 * num2;
        float div = num1 / num2;

        System.out.println(add);
        System.out.println(sub);
        System.out.println(mul);
        System.out.printf("%.2f\n", div);
    }
}