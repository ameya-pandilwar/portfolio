package me.ameyapandilwar;

/**
 * 28. Implement strStr()
 *
 * Implement strStr().
 *
 * Returns the index of the first occurrence of needle in haystack, or -1 if needle is not part of haystack.
 *
 * https://leetcode.com/problems/implement-strstr/
 *
 * Created by ameyapandilwar on 9/22/16.
 */
public class ImplementstrStr {
    public static void main(String[] args) {
        System.out.println(strStr("ameya pandilwar", "pan"));
    }

    private static int strStr(String haystack, String needle) {
        if (haystack == null || needle == null)
            return 0;

        if (needle.length() == 0)
            return 0;

        for (int i = 0; i < haystack.length(); i++) {
            if (i + needle.length() > haystack.length())
                return -1;

            int m = i;
            for (int j = 0; j < needle.length(); j++) {
                if (needle.charAt(j) == haystack.charAt(m)) {
                    if (j == needle.length() - 1)
                        return i;
                    m++;
                } else {
                    break;
                }
            }
        }

        return -1;
    }
}