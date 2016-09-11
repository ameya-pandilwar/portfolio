/**
 * Created by ameyapandilwar on 8/6/16.
 */
public class PalindromeNumber {

    public static void main(String[] args) {
        int[] numbers = new int[] {84126, 84148};

        for (int n : numbers) {
            System.out.println(isPalindrome(n));
        }
    }

    private static boolean isPalindrome(int n) {
        if (n < 0)
            return false;

        int div = 1;
        while (n / div >= 10) {
            div *= 10;
        }

        while (n != 0) {
            if (n / div != n % 10)
                return false;

            n = (n % div) / 10;
            div /= 100;
        }

        return true;
    }
}
