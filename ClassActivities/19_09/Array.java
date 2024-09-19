import java.util.Scanner;

public class Array {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int itemCount = sc.nextInt();

        int[] items = new int[itemCount];

        for (int i = 0; i < itemCount; i++) {
            items[i] = sc.nextInt();
        }
    }
}