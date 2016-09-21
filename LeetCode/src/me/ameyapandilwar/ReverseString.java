package me.ameyapandilwar;

/**
 * 344. Reverse String
 *
 * Write a function that takes a string as input and returns the string reversed.
 *
 * Example:
 * Given s = "hello", return "olleh".
 *
 * https://leetcode.com/problems/reverse-string/
 *
 * Created by ameyapandilwar on 9/20/16.
 */
public class ReverseString {
    public static void main(String[] args) {
        System.out.println(reverseString("hello"));
    }

    private static String reverseString(String s) {
        if (s == null || s.length() == 0)
            return s;

        char[] result = s.toCharArray();
        int length = result.length;

        for (int i = 0; i < length / 2; i++) {
            char temp = result[i];
            result[i] = result[length - i - 1];
            result[length - i - 1] = temp;
        }

        return String.valueOf(result);
    }
}
