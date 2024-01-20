// TO DO: add your implementation and JavaDocs.

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * SimpleList is a singly linked list class that is assembled with nodes.
 * @param <T> is our generic.
 */
class SimpleList<T> implements Iterable<T> {
	
	// A linked list class 
	// You decide the internal attributes and node structure
	// But they should all be private

	// Class for the internal node: not visible to the outside 
	// Do not change the provided fields: otherwise the provided iterator() will not work
	
	//DO NOT CHANGE THIS CLASS EXCEPT TO ADD JAVADOCS
	/**
	 * Node creates our nodes for our SimpleList.
	 * @param <T> is our generic.
	 */
	private class Node<T> {
		
		/**
		 * value holds our data.
		 */
		T value;		// data to store

		/**
		 * next holds our next node.
		 */
		Node<T> next;	// link to the next node
		
		/**
		 * Node creates a node with the given value and a null next.
		 * @param value is the given value.
		 */
		public Node(T value){
			this.value = value;
		}
	}
	
	/**
	 * head stores our head node.
	 */
	private Node<T> head;  	// first node, not dummy

	/**
	 * tail stores our tail node.
	 */
	private Node<T> tail;  	// last node, not dummy

	/**
	 * size stores our list size.
	 */
	private int size;

	/**
	 * holder stores the new node to be added in addLast.
	 */
	private Node<T> holder;

	/**
	 * rmValue holds the data from the node to be removed.
	 */
	private T rmValue;
	// ADD MORE PRIVATE MEMBERS HERE IF NEEDED!
		
	/**
	 * SimpleList creates a new empty list.
	 */
	public SimpleList(){ 
		// Constructor
		// Initialize an empty list
		
		// O(1)

		//The head and tail are null and size is 0.
		this.head = null;
		this.tail = null;
		this.size = 0;
	}
		
	/**
	 * size returns the number of nodes in the list.
	 * @return this.size is the number of nodes.
	 */
	public int size(){
		//Return the number of nodes in list
		//O(1)
		
		return this.size; 
	}

	/**
	 * addLast adds the given value to the tail of the list.
	 * @param value is the given value.
	 */
	public void addLast(T value){
		// Add a new node to the tail of the linked list to hold value
		
		// O(1) 
		
		// Note: The value to be added cannot be null.
		// - Throw IllegalArgumentException if value is null. 
		// - Use this _exact_ error message for the exception 
		//  (quotes are not part of the message):
		//    "Cannot add null value!"

		//An IllegalArgumentException is thrown if value is null.
		if (value == null) {
			throw new IllegalArgumentException("Cannot add null value!");
		}

		//A new node is made with the given value.
		this.holder = new Node<T>(value);

		//It is made both the head and tail if the list is empty.
		if (this.head == null || this.tail == null) {
			this.head = this.holder;
			this.tail = this.holder;
			this.size++;
		}

		//Otherwise it is linked in and made the new tail.
		else {
			this.tail.next = this.holder;
			this.tail = this.holder;
			this.size++;
		}
	}
	
	/**
	 * removeFirst removes the first node and returns its data.
	 * @return this.rmValue is the removed value.
	 */
	public T removeFirst(){
		// Remove the node from the head of the linked list 
		// and return the value from the node.
		// If linked list is empty, return null.
		
		// O(1)
		//If the list is empty, null is returned.
		if (this.head == null) {
			return null;
		}

		//Otherwise, the head data is saved, the head's next is made the new 
		//head (either another node or null if there is only one value in the 
		//list), and the value is returned.
		this.rmValue = this.head.value;
		this.head = this.head.next;
		this.size--;
		return this.rmValue; 
	}

	/**
	 * remove deletes the first node with the given value from the list and 
	 * returns true or false for success or failure.
	 * @param value is the given value.
	 * @return true or false for success or failure.
	 */
	public boolean remove(T value){
		// Given a value, remove the first occurrence of that value
		// Return true if value removed
		// Return false if value not present
		
		// O(N) where N is the number of nodes in list

		//The first node is set and the previous is initialized.
		Node<T> current = this.head;
		Node<T> previous = null;

		//While there's another node, it checks to see if the value matches 
		//any saved values. 
		while (current != null) {
			if (current.value.equals(value)) {
				//If there's only one node, the list is made empty.
				if (this.head == current && this.tail == current) {
					this.head = null;
					this.tail = null;
				}
				//If it is the head, the next value is set for the head.
				else if (this.head == current) {
					this.head = current.next;
				}
				//If it is the tail, the previous value is set as the new tail.
				else if (this.tail == current) {
					previous.next = null;
					this.tail = previous;
				}
				//Otherwise, the node is removed by linking those around it to 
				//each other.
				else {
					previous.next = current.next;
					current.next = null;
				}

				//Size is reduced and true is returned.
				this.size--;
				return true;
			}

			//The current node is saved as the previous one and the next node 
			//is set as the current one.
			previous = current;
			current = current.next;
		}
		// NOTE: remember to use .equals() for comparison

		//If no match found, false is returned.
		return false; 	
	}
	
	/**
	 * get returns the value of the node that matches the requested value.
	 * @param value is the given value.
	 * @return current.value or null are the possible returns.
	 */
	public T get(T value){
		// Find the node with the specified value and
		// *RETURN THE VALUE STORED* from linked list,
		// do NOT return the incoming value.
		// Return null if value is not present.
		
		// O(N) where N is the number of nodes in list

		//The head is set as the current node.
		Node<T> current = this.head;

		//All nodes are checked to see if their value equal the given value.
		while (current != null) {
			//If it matches, the data in the node is returned.
			if (current.value.equals(value)) {
				return current.value;
			}
			//The current node is iterated.
			current = current.next;
		}
		// NOTE: two values might be considered "equivalent" but not identical
		//       example: check Pair class in HashMap.java:
		//				Pair <k,v1> and <k,v2> equal to each other for different v1 and v2 
		// NOTE: remember to use .equals() for comparison

		//If not match is found, null is returned.
		return null;
	}

	// Provided: do not change but you will need to add JavaDoc
	/**
	 * iterator is an iterator for our SimpleList.
	 * @return an iterator.
	 */
	public Iterator<T> iterator(){
		// return a basic iterator of T
		// Note that this method uses your linked list!
		// so if the iterator doesn't work, that's on you...

		//When called, an iterator is created.
		return new Iterator<>(){
			//The start of the iterator is the head.
			private Node<T> current = head;
		
			/**
			 * hasNext returns true or false if there is a next node or not.
			 * @return true or false accordingly.
			 */
			public boolean hasNext(){			
				return (current!=null);
			}
		
			/**
			 * next returns the value of the current node if it has a next 
			 * value.
			 * @return toReturn is the stored value.
			 */
			public T next(){
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				T toReturn = current.value;
				current = current.next;
				return toReturn;
			}
		};
	}
	
	// Provided: do not change but you will need to add JavaDoc
	
	/**
	 * toString concatonates all the value in the SimpleList into a string.
	 * @return s.toString() is the StringBuilder added to made into a string.
	 */
	@Override
	public String toString(){
		// list all values from head to tail and iterates through to do it.
		StringBuilder s = new StringBuilder("[");
		Node<T> current = head;
		String prefix="";
		while (current!=null){
			s.append(prefix);
			s.append(current.value);
			prefix=",";
			current = current.next;
		}
		s.append("]");
		return s.toString();

	}
	
	
	//----------------------------------------------------
	// example testing code... make sure you pass all ...
	// and edit this as much as you want!

	/**
	 * main is our main function for testing.
	 * @param args is command line arguements.
	 */
	public static void main(String[] args){
		//These are _sample_ tests. If you're seeing all the "yays" that's
		//an excellend first step! But it does NOT guarantee your code is 100%
		//working... You may edit this as much as you want, so you can add
		//own tests here, modify these tests, or whatever you need!

		//add, get
		SimpleList<Integer> nums = new SimpleList<Integer>();
		nums.addLast(11);
		nums.addLast(20);
		nums.addLast(5);
		
		if (nums.size()==3 && nums.get(5).equals(5) && 
			nums.get(2) == null){
			System.out.println("Yay 1");
		}
		
		//uncomment to check the list details
		System.out.println(nums);
		System.out.println(nums.size());

		//remove
		if (!nums.remove(16) && nums.remove(11) &&
			nums.get(11)==null && nums.removeFirst().equals(20)){
			System.out.println("Yay 2");			
		} 
		
		//toString and iterator
		nums.addLast(31);
		nums.addLast(10);
		String expectedString = "[5,31,10]";
		Iterator iter = nums.iterator();
		if (nums.toString().equals(expectedString) && iter.hasNext() && 
			iter.next().equals(5) && iter.hasNext() && iter.next().equals(31)){
			System.out.println("Yay 3");						
		}
		
		
	}

}