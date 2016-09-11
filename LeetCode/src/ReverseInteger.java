/**
 * Created by ameyapandilwar on 8/6/16.
 */
public class ReverseInteger {

    public static void main(String[] args) {
        int n = 84126;

        System.out.println(reverseInteger(n));
    }

    private static int reverseInteger(int n) {
        int result = 0;

        while (n != 0) {
            result *= 10;
            result += n % 10;
            n /= 10;
        }

        return result;
    }

}