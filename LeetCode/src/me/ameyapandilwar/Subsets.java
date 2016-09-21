package me.ameyapandilwar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 78. Subsets
 *
 * Given a set of distinct integers, nums, return all possible subsets.
 *
 * Note: The solution set must not contain duplicate subsets.
 *
 * For example,
 * If nums = [1,2,3], a solution is:
 *
 * [
 *   [3],
 *   [1],
 *   [2],
 *   [1,2,3],
 *   [1,3],
 *   [2,3],
 *   [1,2],
 *   []
 * ]
 *
 * https://leetcode.com/problems/subsets/
 *
 * Created by ameyapandilwar on 8/6/16.
 */
public class Subsets {
    public static void main(String[] args) {
        System.out.println(subsets(new int[]{1, 2, 3}));
    }

    private static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> subsets = new ArrayList<>();
        subsets.add(new ArrayList<>());

        Arrays.sort(nums);

        for (int i = 0; i < nums.length; i++) {
            int size = subsets.size();

            for (int j = 0; j < size; j++) {
                List<Integer> subset = new ArrayList<>(subsets.get(j));
                subset.add(nums[i]);
                subsets.add(subset);
            }
        }

        return subsets;
    }
}