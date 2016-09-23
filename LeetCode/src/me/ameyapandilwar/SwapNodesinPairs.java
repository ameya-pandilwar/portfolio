package me.ameyapandilwar;

/**
 * 24. Swap Nodes in Pairs
 *
 * Given a linked list, swap every two adjacent nodes and return its head.
 *
 * For example,
 * Given 1->2->3->4, you should return the list as 2->1->4->3.
 *
 * Your algorithm should use only constant space.
 * You may not modify the values in the list, only nodes itself can be changed.
 *
 * https://leetcode.com/problems/swap-nodes-in-pairs/
 *
 * Created by ameyapandilwar on 9/22/16.
 */
public class SwapNodesinPairs {
    public static void main(String[] args) {
        LinkedListUtils.printLinkedList(swapPairs(LinkedListUtils.createLinkedList(new int[]{1, 2, 3, 4})));
    }

    private static ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null)
            return head;

        ListNode h = new ListNode(0);
        h.next = head;
        ListNode p = h;

        while (p.next != null && p.next.next != null) {
            ListNode t1 = p;
            p = p.next;
            t1.next = p.next;

            ListNode t2 = p.next.next;
            p.next.next = p;
            p.next = t2;
        }

        return h.next;
    }
}