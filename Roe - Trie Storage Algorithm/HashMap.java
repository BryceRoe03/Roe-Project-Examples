//TODO: This class is complete, including JavaDocs.
//  But you may want to run the given main() for more testing of SimpleList / ThreeTenHashSet.

/**
 * A dictionary that maps key->value. 
 *  
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Y. Zhong
 */

class HashMap<K,V> {
	
	/**
	 * A dictionary that decides the entry based on key
	 * and stores key, value pair in the hash table entry.
	 * Keys must be unique.
	 */
	private ThreeTenHashSet<Pair<K,V>> hashTable;
	
	/**
	 * The class representing key,value pair.
 	 * @param <K> the type of keys of the pair
 	 * @param <V> the type of values of the pair
	*/
	private static class Pair<K,V> {
	
		/**
		 * Key in Key,Value pair.
		 */
		K key;

		/**
		 * Value in Key,Value pair.
		 */
		V value;
		
		/**
		 * Constructor.
		 * @param key Key in Key,Value pair.
		 * @param value Value in Key,Value pair.
		 */

		public Pair(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object o) {
			// return true if two pairs have matching keys
			// i.e. <"Alice", 1> is considered as equal to <"Alice", 2>
			if(o instanceof Pair) {
				Pair<K,V> pair = (Pair<K,V>)o;
				return pair.key.equals(key);  
			}
			return false;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			// In order to make sure that keys in a dictionary must be unique,
			// hash code is only determined by key.hashCode().
			return key.hashCode();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "<" + key + "," + value + ">";
		}
		
		/**
		 * Getter for key.
		 * @return Key in Key,Value pair.
		 */
		public K getKey() {
			return key;
		}
		
		/**
		 * Getter for value.
		 * @return Value in Key,Value pair.
		 */
		public V getValue() {
			return value;
		}
	}
	
	/**
	 * Constructor.
	 * @param initLength the starting length of hash table.
	 */
	public HashMap(int initLength){
		//assume initLength >=2
		hashTable = new ThreeTenHashSet<Pair<K,V>>(initLength);	
	}
	
	/**
	 * Add a new pair of key,value in dictionary.
	 * @param key key in key,value pair.
	 * @param value value in key,value pair.
	 * @return true if key,value added; false otherwise.
	 */
	public boolean add(K key, V value) {
		if (key==null || value==null)
			return false;
			
		Pair<K,V> pair = new Pair<>(key, value);
		return hashTable.add(pair);
	}

	/**
	 * Remap key to value in dictionary.
	 * @param key key in key,value pair.
	 * @param value value in key,value pair.
	 * @return true if key is mapped to value after updating; false otherwise.
	 */	
	public boolean update(K key, V value) {
		if (key==null || value==null) 
			return false;
			
		Pair<K,V> pair = new Pair<>(key, value);
		if(!remove(key)) {
			return false;
		}
		return hashTable.add(pair);
	}
	
	/**
	 * Remove key from dictionary.
	 * @param key key to be removed.
	 * @return true if key is removed; false otherwise.
	 */
	public boolean remove(K key) {
		if (key==null)
			return false;
		
		Pair<K,V> pair = new Pair<>(key, null);
		return hashTable.remove(pair);
	}
	
	/**
	 * Report the number of key, value pairs in dictionary.
	 * @return the number of key, value pairs in dictionary.
	 */	
	public int size() {
		return hashTable.size();
	}

	/**
	 * Check whether key is in dictionary.
	 * @param key key to be checked.
	 * @return true if key is present; false otherwise.
	 */
	public boolean contains(K key){
		if (key==null)
			return false;
					
		return hashTable.contains(new Pair<>(key,null));
	}

	/**
	 * Check whether key, value is in dictionary.
	 * @param key key in key,value>pair.
	 * @param value value in key,value pair.
	 * @return true if key,value pair is present; false otherwise.
	 */
	public boolean has(K key, V value){
		if (key==null)
			return false;
			
		Pair<K, V> pair = new Pair<>(key, null);
		return this.contains(key) && (hashTable.get(pair)).getValue().equals(value);
		
	}
		
	/**
	 * Report what value the given key is mapped to in dictionary.
	 * @param key key to be searched.
	 * @return the value this key mapped to in the dictionary; if key not present, return null.	 
	 */
	public V getValue(K key){
	
		if (this.contains(key)){
			Pair<K, V> pair = new Pair<>(key, null);
			return hashTable.get(pair).getValue();
		}
		else
			return null;
	}

	/**
	 * Return all keys in dictionary as a list.
	 * @return the list with all keys in it.	 
	 */
	public SimpleList<K> getKeys(){
		SimpleList<Pair<K,V>> allValues = hashTable.allValues();
		SimpleList<K> keys = new SimpleList<>();
		
		for (Pair<K,V> pair: allValues){
			keys.addLast(pair.getKey());
		}
	
		return keys;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return hashTable.toString();
	}

	//-----------------------------------------------
	// example testing code... edit this as much as you want!
	// This is actually more testing of ThreeTenHashSet / SimpleList.
	// If any "Yay" does not show up, go back to double check your 
	// implementation of SimpleList / ThreeTenHashSet.
	
	/**
	 *  The main method for teting.
	 *  
	 *  @param args not used. 
	 */
	public static void main(String[] args) {
		
		HashMap<String,Integer> nameDict = new HashMap<>(11);
		if ( nameDict.add("Alice",1) && nameDict.contains("Alice") 
			&& !nameDict.contains("Bob") && nameDict.size()==1){
			System.out.println("Yay 1");
		}
		System.out.println(nameDict.toString());
		System.out.println("-----------------------");
		
		//update (key exits: change the mapping to a different value)
		if ( nameDict.has("Alice", 1) && !nameDict.has("Alice", 2) &&
			 nameDict.update("Alice", 10) && !nameDict.update("Bob",2)){
			System.out.println("Yay 2");
		}
		System.out.println(nameDict.toString());
		System.out.println("-----------------------");

		if ( nameDict.getValue("Alice")==10 && nameDict.add("Chris", 20) &&
			 nameDict.remove("Chris") && !nameDict.has("Chris",20)){
			System.out.println("Yay 3");
		}

		System.out.println(nameDict.toString());
		System.out.println("-----------------------");
		
		String expectedString = 
			"ThreeTenHashSet (non-empty entries):\n3 :[<David,32>,<Sophia,3>]\n4 :[<Alice,10>]";
		String nameKeysString = "[David,Sophia,Alice]";
		if (nameDict.add("David",32) && nameDict.add("Sophia",3) &&
			nameDict.toString().equals(expectedString)
			&& nameDict.getKeys().toString().equals(nameKeysString)){
			System.out.println("Yay 4");			
		}
		System.out.println(nameDict.toString());
		System.out.println(nameDict.getKeys().toString());
		System.out.println("-----------------------");
	}
}