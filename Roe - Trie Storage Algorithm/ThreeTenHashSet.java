// TO DO: add your implementation and JavaDocs.

import java.util.Iterator;
/**
 * ThreeTenHashSet creates the hash set that will store our separate chaining
 * array.
 * @param <T> is our generic.
 */
class ThreeTenHashSet<T> {
	// This is the class that you need to write to implement a set 
	// using a hash table with _separate chaining_.

	// Underlying storage table -- you MUST use this for credit!
	// Do NOT change the name or type
	/**
	 * table is the SimpleList array for our separate chaining.
	 */
	private SimpleList<T>[] table;

	// ADD MORE PRIVATE MEMBERS HERE IF NEEDED!

	/**
	 * capacity stores our array length.
	 */
	private int capacity;

	/**
	 * size stores our total number of items in the array.
	 */
	private int size;

	/**
	 * hash stores our hash codes for finding where to store data in the array.
	 */
	private int hash;

	/**
	 * localCapacity keeps track of doubling the size of the array.
	 */
	private int localCapacity;

	/**
	 * holdVal is for storing each value for rehashing.
	 */
	private T holdVal;

	/**
	 * reTable is used to rehash our table.
	 */
	private SimpleList<T>[] retable;

	/**
	 * ThreeTenHashSet creates a new hash set by making an array 
	 * of linked list SimpleLists.
	 * @param initLength is our initial length.
	 */
	@SuppressWarnings("unchecked")
	public ThreeTenHashSet(int initLength){
		// Create a hash table where the storage is with initLength 
		// Initially the table is empty 
		// You can assume initLength is >= 2
		
		// O(1)

		//Our array is made of SimpleLists, size is set to 0, and capacity 
		//is set to initLength.
		this.table = (SimpleList<T>[]) new SimpleList[initLength];
		this.capacity = initLength;
		this.size = 0;
	}

	/**
	 * capacity gives the length of the array.
	 * @return this.capacity which stores it.
	 */
	public int capacity() {
		// return the storage length
		// O(1)
		
		return this.capacity;
	}

	/**
	 * size gives the number of items in the array.
	 * @return this.size which stores it.
	 */
	public int size() {
		// return the number of items in the table
		// O(1)
		
		return this.size;
	}
	
	/**
	 * add inserts a new value into the hash set only if it is not already 
	 * there or null.
	 * @param value is the given value.
	 * @return true or false for success or failure.
	 */
	public boolean add(T value) {
		System.out.println("We adding");
		// Add an item to the set 
		// - return true if you successfully add value; 
		// - return false if the value can not be added
		//    (i.e. the value already exists or is null)

		// NOTES:
		// - Always add value to the tail of the chain.
		// - If load of table is at or above 2.0, rehash() to double the length.
				
		// Time complexity not considering rehash(): 
		// O(N) worst case, where N is the number of values in table
		// O(N/M) average case where N is the number of values in table and M is the table length
		
		//If the value is null, false is returned.
		if (value == null) {
			return false;
		}

		//If the load is > or = 2, the array is rehashed at double capacity.
		//If it doesn't double capacity, false is returned.
		if ((double)this.size() / (double)this.capacity() >= 2.0) {
			this.localCapacity = this.capacity();
			rehash(this.capacity * 2);
			if (this.localCapacity == this.capacity()) {
				return false;
			}
		}

		//The hashcode for our value is generated.
		hash = Math.abs(value.hashCode() % this.capacity);

		//If there is a list in the hash position already, the value is added 
		//to the end of the list. If it is already in that list, false is returned.
		if (this.table[hash] != null) {
			if (this.table[hash].get(value) == null) {
				this.table[hash].addLast(value);
				this.size++;
				return true;
			}
			else {
				return false;
			}
		}
		
		//If there is no list there, a new one is made and the value is added 
		//to it.
		this.table[hash] = new SimpleList<T>();
		this.table[hash].addLast(value);
		this.size++;
		return true;
	}
	
	/**
	 * remove takes the given value out of the set if it is there.
	 * @param value is the given value.
	 * @return true or false if removed or not.
	 */
	public boolean remove(T value) {
		// Removes a value from the set
		// - return true if you remove the item
		// - return false if the item is not present
		
		// O(N) worst case, where N is the number of values in table
		// O(N/M) average case where N is the number of values in table and M is the table length

		//If the given value is null, false.
		if (value == null) {
			return false;
		}

		//The hash for the value is calculated.
		hash = Math.abs(value.hashCode() % this.capacity);

		//It checks the array position where it's supposed to go.
		if (this.table[hash] != null) {
			//If the value is not in the list, false.
			if (this.table[hash].get(value) == null) {
				return false;
			}

			//Otherwise, remove it . If the List is empty, make the position 
			//null.
			else {
				this.table[hash].remove(value);
				this.size--;
				if (this.table[hash].size() == 0) {
					this.table[hash] = null;
				}
				return true;
			}
		}

		return false; 
	}
	
	/**
	 * contains gives true or false if the value is found within the SimpleList.
	 * @param value is the given value.
	 * @return true or false accordingly.
	 */
	public boolean contains(T value) {
		// Return true if value is in the set
		// Return false otherwise
		
		// O(N) worst case, where N is the number of values in table
		// O(N/M) average case where N is the number of values in table and M is the table length

		//If the given value is null, false.
		if (value == null) {
			return false;
		}

		//The hash for the value is calculated.
		hash = Math.abs(value.hashCode() % this.capacity);

		//It checks the array position where it's supposed to go.
		if (this.table[hash] != null) {
			//If it can find the value, true is returned, otherwise, false.
			if (this.table[hash].get(value) != null) {
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * get returns the value from our SimpleList matching the given value.
	 * @param value is the given value.
	 * @return the true value matching from the list.
	 */
	public T get(T value) {
		// Return null if value is not present in set.
		// Return the item _FROM THE HASH TABLE_ if it was found
		//  - do not return the incoming parameter, while "equivalent" they
		// may not be the same)
		
		// O(N) worst case, where N is the number of values in table
		// O(N/M) average case where N is the number of values in table and M is the table length
		
		
		// NOTE: HashMap uses a ThreeTenHashSet of Pair<Key,Value>. In that class,
		// this method is used in the following way:
		//
		// - HashMap passes in a Pair<Key,Value> to search for
		// - The key is "real", the value may be a "dummy" or null
		// - The Pair<Key,--> passed in and the Pair<Key,Value> in the hash table
		//   will match with .equals() -- see equals() in the Pair class
		// - If this method finds the Pair<Key,-->, the returned value must be the 
		//   **actual** hash table entry, which includes both matching key and a valid
		//   non-null value.  
		//
		// Because of this, get() in this class need to be careful too, and it *must*
		// return the value from the hash table and not the parameter.

		//If the given value is null, false.
		if (value == null) {
			return null;
		}

		//The hash for the value is calculated.
		hash = Math.abs(value.hashCode() % this.capacity);

		//It checks the array position where it's supposed to be.
		if (this.table[hash] != null) {
			//If it finds the value within the list there, it returns its 
			//value.
			if (this.table[hash].get(value) != null) {
				return this.table[hash].get(value);
			}
			//Null otherwise.
			else {
				return null;
			}
		}
		//Null if not found.
		return null;
	}
	
	/**
	 * rehash doubles the length of the array and rehashes every value within 
	 * it.
	 * @param newCapacity is the given capacity.
	 * @return true or false for success or failure.
	 */
	@SuppressWarnings("unchecked")
	public boolean rehash(int newCapacity) {
		System.out.println("Rehash time!!");
		if (newCapacity <= this.capacity) {
			System.out.println("Wrong capacity");
			return false;
		}

		//Capacity is updated and a temp array is made with double the space.
		this.capacity = newCapacity;
		this.retable = (SimpleList<T>[]) new SimpleList[newCapacity];
		
		//For the original capacity:
		for (int i = 0; i < this.capacity/2; i++) {
			System.out.println("i");
			//The data holder is initialized and the iterator is made if there 
			//is data there.
			this.holdVal = null;
			if (this.table[i] != null) {
				Iterator listIter = this.table[i].iterator();

				//While there's a next node:
				while (listIter.hasNext()) {

					//The data value is saved and its hashcode is calculated.
					this.holdVal = (T) listIter.next();
					hash = Math.abs(holdVal.hashCode() % this.capacity);

					//If there is not list in the new table, a new one is started.
					if (this.retable[hash] != null) {
						this.retable[hash].addLast(this.holdVal);
					}

					//Otherwise, it is added to the list there.
					else {
						this.retable[hash] = new SimpleList<T>();
						this.retable[hash].addLast(this.holdVal);
					}
				}
			}
			
		}

		//The rehashed table is set as our table and true is returned.
		this.table = this.retable;
		return true;
	}
	
	// Provided: do not change but you will need to add JavaDoc

	/**
	 * toString builds a string of the array of SimpleLists.
	 * @return s.toString().trim() is the trimmed StringBuilder string.
	 */
	@Override
	public String toString() {
		//A new StringBuilder is started.
		StringBuilder s = new StringBuilder("ThreeTenHashSet (non-empty entries):\n");
		//All the values are added and the string is cleaned up.
		for(int i = 0; i < table.length; i++) {
			if(table[i] != null) {
				s.append(i);
				s.append(" :");
				s.append(table[i]);
				s.append("\n");
			}
		}
		return s.toString().trim();
	}
	
	// Provided: do not change but you will need to add JavaDoc

	/**
	 * toStringDebug is the same as toString except it includes the empty entries too.
	 * @return s.toString().trim() is the trimmed StringBuilder string.
	 */
	public String toStringDebug() {
		//A new StringBuilder is started.
		StringBuilder s = new StringBuilder("ThreeTenHashSet (all entries):\n");
		//All the values and empty sections are added and the string is cleaned up.
		for(int i = 0; i < table.length; i++) {
			s.append(i);
			s.append(" :");
			s.append(table[i]);
			s.append("\n");
		}
		return s.toString().trim();
	}

	// Provided: do not change but you will need to add JavaDoc
	/**
	 * allValues returns all the items included in the set as a list.
	 * @return all is the simpleList of all values.
	 */
	public SimpleList<T> allValues(){
		// return all items in set as a list
		SimpleList<T> all = new SimpleList<>();
		for(int i = 0; i < table.length; i++) {
			if (table[i]!=null){
				for (T value: table[i])
					all.addLast(value);
			}
		}
		//Includes duplicate values since it is not a HashSet.
		return all;
	}

	//----------------------------------------------------
	// example testing code... make sure you pass all ...
	// and edit this as much as you want!
	//----------------------------------------------------
	
	/**
	 * main is our main function for testing.
	 * @param args is command line arguements.
	 */
	public static void main(String[] args) {

		// Again, these are limited sample tests.  Showing all "yays" 
		// does NOT guarantee your code is 100%. 
		// You must do more testing.
		System.out.println(46 % 7 + " and " + 46 % 14);
		ThreeTenHashSet<String> names = new ThreeTenHashSet<>(5);
		
		//basic table w/o collision: add / size / capacity
		if(names.add("Alice") && names.add("Bob") && !names.add("Alice") 
			&& names.size() == 2 && names.capacity() == 5) 	{
			System.out.println("Yay 1");
		}
		System.out.println(names.toString());
		System.out.println("-----------------------");
		System.out.println(names.toStringDebug());
		System.out.println("-----------------------");
		
		//remove / contains / get
		if(!names.remove("Alex") && names.remove("Bob") && names.contains("Alice") 
			&& !names.contains("Bob") && names.get("Bob")==null) {
			System.out.println("Yay 2");
		}
		//System.out.println(names.toString());
		//System.out.println("-----------------------");
		
		//table with collision
		ThreeTenHashSet<Integer> nums = new ThreeTenHashSet<>(5);
		for(int i = 0; i <7 ; i++) {
			nums.add(i);
		}
		String expectedString = 
			"ThreeTenHashSet (non-empty entries):\n0 :[0,5]\n1 :[1,6]\n2 :[2]\n3 :[3]\n4 :[4]";
		String allValueString = "[0,5,1,6,2,3,4]";
		if (nums.size()==7 && nums.toString().equals(expectedString)
			&& nums.allValues().toString().equals(allValueString)){
			System.out.println("Yay 3");			
		}
		System.out.println(nums.toString());		

		//rehash
		String rehashedString = 
			"ThreeTenHashSet (non-empty entries):\n0 :[0]\n1 :[1]\n2 :[2]\n3 :[3]\n4 :[4]\n5 :[5]\n6 :[6]";

		
		if (!nums.rehash(3) && nums.rehash(11) && nums.capacity()==11
			&& nums.toString().equals(rehashedString)){
			System.out.println("Yay 4");					
		}		
		
		/*if (nums.capacity()==11) {
			System.out.println("It works\n");
		}*/
		System.out.println(nums.toString());		
		
		
	}
}