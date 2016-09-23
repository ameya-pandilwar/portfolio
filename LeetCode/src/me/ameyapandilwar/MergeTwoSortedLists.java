package me.ameyapandilwar;

/**
 * 21. Merge Two Sorted Lists
 *
 * Merge two sorted linked lists and return it as a new list.
 * The new list should be made by splicing together the nodes of the first two lists.
 *
 * https://leetcode.com/problems/merge-two-sorted-lists/
 *
 * Created by ameyapandilwar on 9/22/16.
 */
public class MergeTwoSortedLists {
    public static void main(String[] args) {
        LinkedListUtils.printLinkedList(
                mergeTwoLists(
                        LinkedListUtils.createLinkedList(new int[]{1, 2, 3, 5, 7}),
                        LinkedListUtils.createLinkedList(new int[]{3, 5, 8, 9})
                )
        );
    }

    private static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode head = new ListNode(0);
        ListNode node = head;

        while (l1 != null || l2 != null) {
            if (l1 != null && l2 != null) {
                if (l1.val < l2.val) {
                    node.next = l1;
                    l1 = l1.next;
                } else {
                    node.next = l2;
                    l2 = l2.next;
                }
                node = node.next;
            } else if (l1 == null) {
                node.next = l2;
                break;
            } else {
                node.next = l1;
                break;
            }
        }

        return head.next;
    }
}