package me.ameyapandilwar;

import java.util.Arrays;

/**
 * 4. Median of Two Sorted Arrays
 *
 * There are two sorted arrays nums1 and nums2 of size m and n respectively.
 *
 * Find the median of the two sorted arrays. The overall run time complexity should be O(log (m+n)).
 *
 * Example 1:
 * nums1 = [1, 3]
 * nums2 = [2]
 *
 * The median is 2.0
 *
 * Example 2:
 * nums1 = [1, 2]
 * nums2 = [3, 4]
 *
 * The median is (2 + 3)/2 = 2.5
 *
 * https://leetcode.com/problems/median-of-two-sorted-arrays/
 *
 * Created by ameyapandilwar on 9/21/16.
 */
public class MedianofTwoSortedArrays {
    public static void main(String[] args) {
        System.out.println(findMedianSortedArrays(new int[]{1, 3}, new int[]{2}));
    }

    private static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1 == null || nums2 == null) {
            return 0.f;
        }

        int n1 = nums1.length;
        int n2 = nums2.length;

        if ((n1 + n2) % 2 == 1) {
            return findMedianHelper(nums1, nums2, (n1 + n2) / 2 + 1);
        } else {
            return (findMedianHelper(nums1, nums2, (n1 + n2) / 2) + findMedianHelper(nums1, nums2, (n1 + n2) / 2 + 1)) / 2;
        }
    }

    private static double findMedianHelper(int[] nums1, int[] nums2, int k) {
        if (nums1 == null || nums1.length == 0) {
            return nums2[k - 1];
        }

        if (nums2 == null || nums2.length == 0) {
            return nums1[k - 1];
        }

        if (k == 1) {
            return Math.min(nums1[0], nums2[0]);
        }

        int n1 = nums1.length;
        int n2 = nums2.length;

        if (nums1[n1 / 2] > nums2[n2 / 2]) {
            if ((n1 / 2 + n2 / 2 + 1) >= k) {
                return findMedianHelper(Arrays.copyOfRange(nums1, 0, n1 / 2), nums2, k);
            } else {
                return findMedianHelper(nums1, Arrays.copyOfRange(nums2, n2 / 2 + 1, n2), k - (n2 / 2 + 1));
            }
        } else {
            if ((n1 / 2 + n2 / 2 + 1) >= k) {
                return findMedianHelper(nums1, Arrays.copyOfRange(nums2, 0, n2 / 2), k);
            } else {
                return findMedianHelper(Arrays.copyOfRange(nums1, n1 / 2 + 1, n1), nums2, k - (n1 / 2 + 1));
            }
        }
    }
}
