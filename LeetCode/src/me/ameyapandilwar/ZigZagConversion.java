package me.ameyapandilwar;

/**
 * 6. ZigZag Conversion
 *
 * The string "PAYPALISHIRING" is written in a zigzag pattern on a given number of rows like this:
 * (you may want to display this pattern in a fixed font for better legibility)
 *
 * P   A   H   N
 * A P L S I I G
 * Y   I   R
 *
 * And then read line by line: "PAHNAPLSIIGYIR"
 * Write the code that will take a string and make this conversion given a number of rows:
 *
 * string convert(string text, int nRows);
 * convert("PAYPALISHIRING", 3) should return "PAHNAPLSIIGYIR".
 *
 * https://leetcode.com/problems/zigzag-conversion/
 *
 * Created by ameyapandilwar on 9/11/16.
 */
public class ZigZagConversion {
    public static void main(String[] args) {
        System.out.println(convert("PAYPALISHIRING", 3));
        System.out.println(convert("ABC", 2));
    }

    private static String convert(String s, int numRows) {
        if (s == null || s.length() == 0 || numRows <= 0)
            return "";
        if (numRows == 1) return s;

        StringBuilder sb = new StringBuilder();
        int size = 2 * numRows - 2;

        for (int i = 0; i < numRows; i++) {
            for (int j = i; j < s.length(); j += size) {
                sb.append(s.charAt(j));

                if (i != 0 && i != numRows - 1 && (j + size - 2 * i) < s.length()) {
                    sb.append(s.charAt(j + size - 2 * i));
                }
            }
        }

        return sb.toString();
    }
}
