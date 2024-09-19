import java.util.Scanner;

public class Array {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int itemCount = sc.nextInt();

        double[] itemPrice = new double[itemCount];

        for (int i = 0; i < itemCount; i++) {
            itemPrice[i] = sc.nextDouble();
        }

        sc.close();

        double totalPrice = 0;

        for (int i = 0; i < itemCount; i++) {
            totalPrice += itemPrice[i];
        }

        System.out.println("Total price: " + totalPrice);
        
        double maxPrice = itemPrice[0];
        double minPrice = itemPrice[0];

        for (int i = 1; i < itemCount; i++) {
            if (itemPrice[i] > maxPrice) {
                maxPrice = itemPrice[i];
            }

            if (itemPrice[i] < minPrice) {
                minPrice = itemPrice[i];
            }
        }

        System.out.println("Max price: " + maxPrice);
        System.out.println("Min price: " + minPrice);
    }
}