package me.ameyapandilwar;

/**
 * 237. Delete Node in a Linked List
 *
 * Write a function to delete a node (except the tail) in a singly linked list, given only access to that node.
 *
 * Supposed the linked list is 1 -> 2 -> 3 -> 4 and you are given the third node with value 3,
 * the linked list should become 1 -> 2 -> 4 after calling your function.
 *
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 *
 * https://leetcode.com/problems/delete-node-in-a-linked-list/
 *
 * Created by ameyapandilwar on 9/22/16.
 */
public class DeleteNodeinaLinkedList {

    public static void main(String[] args) {
        int[] elements = new int[]{1, 2, 3, 4};
        ListNode node = new ListNode(elements[0]);
        ListNode current = LinkedListUtils.nodeToBeDeleted(node, elements, 3);

        LinkedListUtils.printLinkedList(node);

        deleteNode(current);

        LinkedListUtils.printLinkedList(node);
    }

    private static void deleteNode(ListNode node) {
        if (node != null && node.next != null) {
            node.val = node.next.val;
            node.next = node.next.next;
        }
    }
}