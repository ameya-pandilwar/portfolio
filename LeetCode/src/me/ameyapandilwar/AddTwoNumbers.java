package me.ameyapandilwar;

/**
 * 2. Add Two Numbers
 *
 * You are given two linked lists representing two non-negative numbers.
 * The digits are stored in reverse order and each of their nodes contain a single digit.
 * Add the two numbers and return it as a linked list.
 *
 * Input: (2 -> 4 -> 3) + (5 -> 6 -> 4)
 * Output: 7 -> 0 -> 8
 *
 * https://leetcode.com/problems/add-two-numbers/
 *
 * Created by ameyapandilwar on 9/10/16.
 */
public class AddTwoNumbers {
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }

    public static void main(String[] args) {
        ListNode node;

        ListNode l1 = new ListNode(2);
        node = l1;
        node.next = new ListNode(4);
        node = node.next;
        node.next = new ListNode(3);

        ListNode l2 = new ListNode(5);
        node = l2;
        node.next = new ListNode(6);
        node = node.next;
        node.next = new ListNode(4);

        ListNode result = addTwoNumbers(l1, l2);
        while (result != null) {
            System.out.print(result.val);
            result = result.next;
            if (result != null) {
                System.out.print(" -> ");
            }
        }
    }

    private static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummyHead = new ListNode(0);
        ListNode p = l1, q = l2, curr = dummyHead;
        int carry = 0;
        while (p != null || q != null) {
            int x = (p != null) ? p.val : 0;
            int y = (q != null) ? q.val : 0;
            int sum = carry + x + y;
            carry = sum / 10;
            curr.next = new ListNode(sum % 10);
            curr = curr.next;
            if (p != null) p = p.next;
            if (q != null) q = q.next;
        }
        if (carry > 0) {
            curr.next = new ListNode(carry);
        }
        return dummyHead.next;
    }
}