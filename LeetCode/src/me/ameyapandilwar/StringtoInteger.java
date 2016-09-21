package me.ameyapandilwar;

/**
 * 8. String to Integer (atoi)
 * 
 * Implement atoi to convert a string to an integer.
 * 
 * Hint: Carefully consider all possible input cases.
 * If you want a challenge, please do not see below and ask yourself what are the possible input cases.
 * 
 * Notes: It is intended for this problem to be specified vaguely (ie, no given input specs). 
 * You are responsible to gather all the input requirements up front.
 *
 * https://leetcode.com/problems/string-to-integer-atoi/
 *
 * Created by ameyapandilwar on 9/20/16.
 */
public class StringtoInteger {
    public static void main(String[] args) {
        System.out.println(myAtoi("1"));
    }

    private static int myAtoi(String str) {
        if (str == null || str.length() < 1)
            return 0;

        str = str.trim();

        char flag = '+';

        int i = 0;
        if (str.charAt(0) == '-') {
            flag = '-';
            i++;
        } else if (str.charAt(0) == '+') {
            i++;
        }

        double result = 0;

        while (str.length() > i && str.charAt(i) >= '0' && str.charAt(i) <= '9') {
            result = result * 10 + (str.charAt(i) - '0');
            i++;
        }

        if (flag == '-')
            result = -result;

        if (result > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;

        if (result < Integer.MIN_VALUE)
            return Integer.MIN_VALUE;

        return (int) result;
    }
}