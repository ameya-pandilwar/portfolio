package me.ameyapandilwar;

/**
 * Created by ameyapandilwar on 9/22/16.
 */
public class LinkedListUtils {

    static void printLinkedList(ListNode node) {
        while (node != null) {
            System.out.print(node.val);
            node = node.next;
            System.out.print(node != null ? " -> " : "\n");
        }
    }

    static ListNode nodeToBeDeleted(ListNode current, int[] elements, int x) {
        ListNode node = null;
        for (int i = 1; i < elements.length; i++) {
            current.next = new ListNode(elements[i]);
            current = current.next;
            if (elements[i] == x)
                node = current;
        }
        return node;
    }

    static ListNode createLinkedList(int[] elements) {
        ListNode node = new ListNode(0), current = node;
        for (int i = 0; i < elements.length; i++) {
            current.next = new ListNode(elements[i]);
            current = current.next;
        }
        return node.next;
    }
}