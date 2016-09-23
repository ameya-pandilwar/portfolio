package me.ameyapandilwar;

/**
 * 206. Reverse Linked List
 *
 * Reverse a singly linked list.
 *
 * Hint:
 * A linked list can be reversed either iteratively or recursively. Could you implement both?
 *
 * https://leetcode.com/problems/reverse-linked-list/
 *
 * Created by ameyapandilwar on 9/22/16.
 */
public class ReverseLinkedList {
    public static void main(String[] args) {
        ListNode nodeIterative = LinkedListUtils.createLinkedList(new int[]{1, 2, 3, 4, 5});
        ListNode nodeRecursive = LinkedListUtils.createLinkedList(new int[]{1, 2, 3, 4, 5});

        LinkedListUtils.printLinkedList(nodeIterative);

        ListNode reversedIterative = reverseListIterative(nodeIterative);
        ListNode reversedRecursive = reverseListRecursive(nodeRecursive);

        LinkedListUtils.printLinkedList(reversedIterative);
        LinkedListUtils.printLinkedList(reversedRecursive);
    }

    private static ListNode reverseListIterative(ListNode head) {
        ListNode previous = null, current = head;
        while (current != null) {
            ListNode node = current.next;
            current.next = previous;
            previous = current;
            current = node;
        }
        return previous;
    }

    private static ListNode reverseListRecursive(ListNode head) {
        if (head == null || head.next == null)
            return head;
        ListNode node = reverseListRecursive(head.next);
        head.next.next = head;
        head.next = null;
        return node;
    }
}