package me.ameyapandilwar;

/**
 * 9. Palindrome Number
 *
 * Determine whether an integer is a palindrome. Do this without extra space.
 *
 * https://leetcode.com/problems/palindrome-number/
 *
 * Created by ameyapandilwar on 8/6/16.
 */
public class PalindromeNumber {
    public static void main(String[] args) {
        int[] numbers = new int[]{84126, 84148};

        for (int n : numbers) {
            System.out.println(isPalindrome(n));
        }
    }

    private static boolean isPalindrome(int x) {
        if (x < 0)
            return false;

        int div = 1;
        while (x / div >= 10) {
            div *= 10;
        }

        while (x != 0) {
            if (x / div != x % 10)
                return false;

            x = (x % div) / 10;
            div /= 100;
        }

        return true;
    }
}