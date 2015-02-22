package in.webrilliance.linkedlists;

/**
 * @author Ameya
 *
 */
class NodeHelper {
	
	/**
	 * @param head
	 * @param data
	 */
	void appendToTail(Node head, int data) {
		Node end = new Node(data);
		while (head.next != null) {
			head = head.next;
		}
		head.next = end;
	}
	
	/**
	 * @param head
	 * @param data
	 * @return
	 */
	Node deleteNode(Node head, int data) {
		Node node = head;
		
		if (node.data == data) {
			return head.next;
		}
		
		while (node.next != null) {
			if (node.next.data == data) {
				node.next = node.next.next;
				return head;
			}
			node = node.next;
		}
		return head;
	}

}