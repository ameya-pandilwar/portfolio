package me.ameyapandilwar;

/**
 * 10. Regular Expression Matching
 *
 * Implement regular expression matching with support for '.' and '*'.
 *
 * '.' Matches any single character.
 * '*' Matches zero or more of the preceding element.
 *
 * The matching should cover the entire input string (not partial).
 *
 * The function prototype should be:
 * bool isMatch(const char *s, const char *p)
 *
 * Some examples:
 * isMatch("aa","a") → false
 * isMatch("aa","aa") → true
 * isMatch("aaa","aa") → false
 * isMatch("aa", "a*") → true
 * isMatch("aa", ".*") → true
 * isMatch("ab", ".*") → true
 * isMatch("aab", "c*a*b") → true
 *
 * https://leetcode.com/problems/regular-expression-matching/
 *
 * Created by ameyapandilwar on 9/21/16.
 */
public class RegularExpressionMatching {
    public static void main(String[] args) {
        System.out.println(isMatch("aa", "a"));
    }

    private static boolean isMatch(String s, String p) {
        if (p == null || p.length() == 0) {
            return s == null || s.length() == 0;
        }
        int rows = s.length();
        int cols = p.length();

        boolean[][] dp = new boolean[rows + 1][cols + 1];
        dp[0][0] = true;

        for (int j = 1; j <= cols; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                char sChar = s.charAt(i - 1);
                char pChar = p.charAt(j - 1);

                if (pChar != '*') {
                    if (sChar == pChar || pChar == '.') {
                        dp[i][j] = dp[i - 1][j - 1];
                    }
                } else {
                    if (sChar != p.charAt(j - 2) && p.charAt(j - 2) != '.') {
                        dp[i][j] = dp[i][j - 2];
                    } else {
                        dp[i][j] = dp[i][j - 2] || dp[i - 1][j] || dp[i][j - 1];
                    }
                }
            }
        }
        return dp[rows][cols];
    }
}