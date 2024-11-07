
import java.util.Scanner;

public class doubleArray {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int itemCount = sc.nextInt();

        double[] itemPrice = new double[itemCount];
        int[] itemQuantity = new int[itemCount];
        double[][] itemDetails = new double[itemCount][3]; // 0: price, 1: quantity, 2: total

        for (int i = 0; i < itemCount; i++) {
            itemPrice[i] = sc.nextDouble();
            itemQuantity[i] = sc.nextInt();
            itemDetails[i][0] = itemPrice[i];
            itemDetails[i][1] = itemQuantity[i];
            itemDetails[i][2] = itemPrice[i] * itemQuantity[i];
        }

        sc.close();

        double totalPrice = 0;

        for (int i = 0; i < itemCount; i++) {
            totalPrice += itemDetails[i][2];
        }

        System.out.println("Total price: " + totalPrice);

        double maxPrice = itemDetails[0][0];

        for (int i = 1; i < itemCount; i++) {
            if (itemDetails[i][0] > maxPrice) {
                maxPrice = itemDetails[i][0];
            }
        }

        System.out.println("Max price: " + maxPrice);

        double minPrice = itemDetails[0][0];

        for (int i = 1; i < itemCount; i++) {
            if (itemDetails[i][0] < minPrice) {
                minPrice = itemDetails[i][0];
            }
        }

        System.out.println("Min price: " + minPrice);

        double maxQuantity = itemDetails[0][1];

        for (int i = 1; i < itemCount; i++) {
            if (itemDetails[i][1] > maxQuantity) {
                maxQuantity = itemDetails[i][1];
            }
        }

        System.out.println("Max quantity: " + maxQuantity);
    }
}
