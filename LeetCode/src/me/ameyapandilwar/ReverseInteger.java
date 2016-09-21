package me.ameyapandilwar;

/**
 * 7. Reverse Integer
 *
 * Reverse digits of an integer.
 *
 * Example1: x = 123, return 321
 * Example2: x = -123, return -321
 *
 * https://leetcode.com/problems/reverse-integer/
 *
 * Created by ameyapandilwar on 9/11/16.
 */
public class ReverseInteger {
    public static void main(String[] args) {
        int[] numbers = new int[]{123, -123, 1534236469};

        for (int n : numbers) {
            System.out.println(reverseInteger(n));
        }
    }

    private static int reverseInteger(int x) {
        long result = 0;

        while (x != 0) {
            result = (result * 10) + (x % 10);
            x /= 10;
        }

        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            return 0;
        }

        return (int) result;
    }
}