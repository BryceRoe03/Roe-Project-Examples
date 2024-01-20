// TO DO: add your implementation and JavaDocs.

/**
 * StrieNode creates the nodes for our Strie.
 */
public class StrieNode{

	// Use a HashMap to hold children nodes.
	// Keys of the map can be any Character while values are the children nodes.
	// Each key in the map leads to a child node of this node.

	/**
	 * children holds the character it represents and the link to the next node.
	 */
	private HashMap<Character, StrieNode> children; 

	// Marks the end of a word
	/**
	 * endMarker keeps track of where words end.
	 */
	private boolean endMarker;  
	
	// OPTIONAL boolean flag that you can use.
	// It is completely optional to use this in your implementation.
	// We will NOT test its usage but it is provided for more flexibility.
	// Still, remember to write JavaDoc for it.

	/**
	 * flag is an optional flag we can track.
	 */
	private boolean flag;  	
	
	/**
	 * INIT_MAP_LENGTH is the default hashmap length.
	 */
	private static final int INIT_MAP_LENGTH = 5; //default length of the hashmap to start

	// ADD MORE PRIVATE MEMBERS HERE IF NEEDED
	//private HashMap<T> hashMap;
	//private int numChild;
	//private boolean end;

	/**
	 * StrieNode constructs a new node for our Strie.
	 */
	public StrieNode(){
		// Constructor
		// Initialize anything that needs initialization
		// HashMap must start with INIT_MAP_LENGTH entries
		this.children = new HashMap<Character, StrieNode>(INIT_MAP_LENGTH);
		this.endMarker = false;
		this.flag = false;
		// O(1)
	}

	/**
	 * getNumChildren returns the number of child nodes.
	 * @return this.children.size() is our total number.
	 */
	public int getNumChildren(){
		//report number of children nodes
		//O(1)
		
		return this.children.size(); 	//default return; change or remove as needed	
	}

	/**
	 * getAllChildren returns the HashMap of all the children.
	 * @return this.children is the HashMap of children.
	 */
	public HashMap<Character, StrieNode> getAllChildren(){
		// return the storage of all children
		// O(1)
		
		return this.children;//default return; change or remove as needed	
	}

	/**
	 * setEnd changes the endMarker for a word to true.
	 */
	public void setEnd(){
		// Sets the end marker to indicate this node is the end of a string/word
		// O(1)

		this.endMarker = true;
	}
	
	/**
	 * unsetEnd changes the endMarker for a word to false.
	 */
	public void unsetEnd(){
		// Unsets the end marker
		// O(1)

		this.endMarker = false;
	}
	
	/**
	 * isEnd returns what the endMarker value holds.
	 * @return true or false accordingly.
	 */
	public boolean isEnd(){
		// Checks whether the current node is marked as the end of a string/word
		// O(1)
		
		return this.endMarker;
	}
	
	/**
	 * containsChild checks if there is a child node in the chain with the 
	 * //given character.
	 * @param ch is the given character.
	 * @return true or false accordingly.
	 */
	public boolean containsChild(char ch){
		// returns true if node has a child node corresponding to ch;
		// return false otherwise 

		// O(1)
		// You can assume all HashMap operations are O(1)

		//Returns true if a node among the children with the character is 
		//there.
		if (this.children.contains(ch)) {
			return true;
		}
		return false;
	}

	/**
	 * getChild returns the StrieNode that contains the character given and 
	 * null if there is no node.
	 * @param ch is the given character.
	 * @return this.children.getValue(ch) is either the node or null.
	 */
	public StrieNode getChild(char ch){
		// returns the child node corresponding to ch
		// returns null if no such node

		// O(1)
		// You can assume all HashMap operations are O(1)
		
		return this.children.getValue(ch);
	}

	/**
	 * putChild either creates a new HashMap entry for the given character and 
	 * node or updates the preexisting one with the same character in the area.
	 * @param ch is the given character.
	 * @param node is the given node.
	 */
	public void putChild(char ch, StrieNode node){
		// set a child node corresponding to ch to node
		// if a node already exists, change the mapping of ch to the specified node

		// O(1)
		// You can assume all HashMap operations are O(1) except getKeys() and toString()
		if (this.children.contains(ch)) {
			this.children.update(ch, node);
		}
		else {
			this.children.add(ch, node);
		}
	}
		
	/**
	 * removeChild removes the given character child from the StrieNode if it 
	 * is there.
	 * @param ch the given character.
	 * @return true or false accordingly.
	 */
	public boolean removeChild(char ch){
		// remove child node corresponding to ch if a node is present
		// return true if a child was removed;
		// if no such child node, return false
	
		// O(1)
		// You can assume all HashMap operations are O(1) except getKeys() and toString()
		if (!this.children.contains(ch)) {
			return false;
		}
		this.children.remove(ch);
		return true; //default return; change or remove as needed	
	}

	// Below are methods with the optional flag
	// - implementation of those are optional 
	// - no testing on them in grading
	// Still, remember to write JavaDoc for them.

	/**
	 * setFlag sets the flag to true.
	 */
	public void setFlag(){
		// set the optional flag to be true
		// O(1)
		this.flag = true;
	}

	/**
	 * unSetFlag sets the flag to false.
	 */
	public void unSetFlag(){
		// set the optional flag to be false
		// O(1)
		this.flag = false;
	}
	
	/**
	 * checkFlag returns the flag's value.
	 * @return this.flag.
	 */
	public boolean checkFlag(){
		// report the status of the optional flag
		// O(1)
		
		return this.flag;	
	}

}
