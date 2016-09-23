package me.ameyapandilwar;

/**
 * 141. Linked List Cycle
 *
 * Given a linked list, determine if it has a cycle in it.
 *
 * Follow up:
 * Can you solve it without using extra space?
 *
 * https://leetcode.com/problems/linked-list-cycle/
 *
 * Created by ameyapandilwar on 9/22/16.
 */
public class LinkedListCycle {
    public static void main(String[] args) {
        System.out.println(hasCycle(LinkedListUtils.createLinkedList(new int[]{1, 2, 3, 4, 5})));
    }

    private static boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast)
                return true;
        }

        return false;
    }
}