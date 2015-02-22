package in.webrilliance.linkedlists;

/**
 * Write code to remove duplicates fom an unsorted linked list.
 * FOLLOW UP
 * How would you solve this problem if a temporary buffer is not allowed?
 */

/**
 * @author Ameya
 *
 */
public class SolutionTwoPointOne {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Node a = new Node(2);
		Node b = new Node(4);
		Node c = new Node(3);
		Node d = new Node(1);
		Node e = new Node(3);
		Node f = new Node(4);

		a.next = b; b.next = c; c.next = d; d.next = e; e.next = f;

		Node head = a;
		SolutionTwoPointOne solution = new SolutionTwoPointOne();
		solution.printLinkedList(head);
		head = solution.deleteDuplicateNode(head, 3);
		System.out.println("\n===========");
		solution.printLinkedList(head);
	}

	private Node deleteDuplicateNode(Node head, int data) {
		boolean duplicateOccurence = false;
		Node node = head;
		while (node.next != null) {
			if (node.next.data == data) {
				if (!duplicateOccurence) {
					duplicateOccurence = true;
					node = node.next;
				} else {
					node.next = node.next.next;
				}
			} else {
				node = node.next;
			}
		}
		return head;
	}

	private void printLinkedList(Node head) {
		while (head.next != null) {
			System.out.print(head.data + " ");
			head = head.next;
		}
		System.out.print(head.data);
	}
}
