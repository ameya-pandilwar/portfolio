package me.ameyapandilwar;

/**
 * 5. Longest Palindromic Substring
 *
 * Given a string S, find the longest palindromic substring in S.
 * You may assume that the maximum length of S is 1000, and there exists one unique longest palindromic substring.
 *
 * https://leetcode.com/problems/longest-palindromic-substring/
 *
 * Created by ameyapandilwar on 9/21/16.
 */
public class LongestPalindromicSubstring {
    public static void main(String[] args) {
        System.out.println(longestPalindrome("a"));
    }

    private static String longestPalindrome(String s) {
        int len = s.length();
        if (len <= 1) return s;

        int start = 0;
        int maxLen = 0;

        for (int i = 1; i < len; i++) {
            int lo = i - 1;
            int hi = i;

            while (lo >= 0 && hi < len && s.charAt(lo) == s.charAt(hi)) {
                lo--;
                hi++;
            }

            if ((hi - lo - 1) > maxLen) {
                maxLen = hi - lo - 1;
                start = lo + 1;
            }

            lo = i - 1;
            hi = i + 1;

            while (lo >= 0 && hi < len && s.charAt(lo) == s.charAt(hi)) {
                lo--;
                hi++;
            }

            if ((hi - lo - 1) > maxLen) {
                maxLen = hi - lo - 1;
                start = lo + 1;
            }
        }

        return s.substring(start, start + maxLen);
    }
}
