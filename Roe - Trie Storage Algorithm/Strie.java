

// TO DO: add your implementation and JavaDocs.

/**
 * Strie is our whole tree of words itself and its highest functions.
 */
public class Strie{
	//----------------------------------------------------
	// NO MORE INSTANCE VARIABLES ALLOWED!
	//----------------------------------------------------
	
	// Do NOT change the name or type of these variables

	/**
	 * root holds the root of our strie.
	 */
	private StrieNode root;  // the root of a strie

	/**
	 * numWords tracks the number of full words in our strie.
	 */
	private int numWords = 0; // number of words represented by the strie
	
	//----------------------------------------------------
	// NO MORE INSTANCE VARIABLES ALLOWED!
	//----------------------------------------------------
	
	/**
	 * Strie constructs a new strie with an empty node and no words.
	 */
	public Strie(){
		// Constructor
		// Initialize root to be an empty node; initially no words are in the strie
		this.root = new StrieNode();
		this.numWords = 0;
		// O(1)
	}
	
	/**
	 * numWords give the number of words in our strie.
	 * @return this.numWords.
	 */
	public int numWords(){
		// return number of words in the strie
		
		// O(1)
	
		return this.numWords;
	}
	
	/**
	 * getRoot gives the root of our strie.
	 * @return this.root.
	 */
	public StrieNode getRoot(){
		// return root of the strie
		
		// O(1)
		
		return this.root;	
	
	}

	/**
	 * insert puts the word into our strie if it's not there.
	 * @param word the given word.
	 */
	public void insert(String word){
		// Insert word into your Strie. 

		// O(n) where n is the number of characters in word

		//The root is made the starting place.
		StrieNode current = this.root;
		// System.out.println("Word is "+ word);
		for (int i = 0; i < word.length(); i++) {
			//If there isn't a position for the character, one is made.
			if (!current.containsChild(word.charAt(i))) {
				// System.out.println("Char is "+ word.charAt(i));
				current.putChild(word.charAt(i), new StrieNode());
			}
			//The position is updated so the next character can be added.
			current = current.getChild(word.charAt(i));

			//The ending of the word is set.
			if (i == word.length() - 1) {
				if (current.isEnd()) {
					continue;
				}
				else {
					current.setEnd();
					// System.out.println("Made it to the end with "+ word);
					this.numWords++;
				}
			}
		}
	}

	/**
	 * contains checks if the word is in our Strie.
	 * @param word is the given word.
	 * @return true or false accordingly.
	 */
	public boolean contains(String word){
		// Returns true if Strie contains the given word.
		// System.out.println("We check contains");
		// O(n) where n is the number of characters in word

		//Start with our root.
		StrieNode current = this.root;
		//Check for the length of the word.
		for (int i = 0; i < word.length(); i++) {
			//Follow through each letter it has and return false if it is not 
			//found.
			if (!current.containsChild(word.charAt(i))) {
				// System.out.println(word.charAt(i) + " is not here");
				return false;
			}
			//Iterate to the next node.
			current = current.getChild(word.charAt(i));
			//If at the end of the word, if it is the end, true.
			if (i == word.length() - 1) {
				if (current.isEnd()) {
					return true;
				}
				else {
					continue;
				}
			}
		}
		//If not, false.
		return false;
	}

	/**
	 * remove takes out the given word from our Strie if it is there.
	 * @param word is the given word.
	 * @return true or false accordingly.
	 */
	public boolean remove(String word){
		// Removes the given word from Strie.
		
		// If word is not present in strie, return false;
		// Otherwise, remove word and return true.
		
		// Hint: Consider using recursion in your implementation.
		// Hint: You can define recursive helper functions.
		
		// Note: While there are **no Big-O restrictions** on this
		// method, it can be done in O(n) where n is the number
		// of characters in word
		
		//Checks if the word is in our Strie.
		if (this.contains(word)) {
			//Track our current StrieNode and the last branch in the Strie.
			StrieNode current = this.root;
			StrieNode lastBranch = this.root;

			//Track the letter number after the branch.
			int letterAfter = 0;
			
			//Track the last full word stop and its next number position.
			StrieNode lastEnd = null;
			int letterAfterEnd = 0;

			   //For as long as the word is.
		    for (int i = 0; i < word.length(); i++) {

				   //Iterate to the next Node.
		    	current = current.getChild(word.charAt(i));

				//If has other letters branching off, track it.
				if (current.getNumChildren() > 1) {
					
					lastBranch = current;
					letterAfter = i+1;
				}
				//If is an end but not our word length, update nearest last.
				if (current.isEnd() && i != (word.length() -1)) {
					lastEnd = current;
					letterAfterEnd = i+1;
				}
				//If is the length of the word, check for children.
				if (i == (word.length() -1)) {
					//Set as not end to not ruin other words, reduce count and return true.
					if (current.getNumChildren() > 0) {
						current.unsetEnd();
						this.numWords--;
						return true;
					}
					
					else {
						//Free to clip the branch.
						if (lastBranch.getNumChildren() == 0) {
							this.root.removeChild(word.charAt(letterAfter));
							this.numWords--;
							return true;
						}

						//Clip from the correct position to not delete other words.
						else {
							if (lastEnd != null && lastEnd.isEnd()) {
								lastEnd.removeChild(word.charAt(letterAfterEnd));
								this.numWords--;
								return true;
							}
							lastBranch.removeChild(word.charAt(letterAfter));
							this.numWords--;
							return true;
						}
					}
				}
				
		    }
			return true;
		}

		return false;	
	}

	/**
	 * levelOrderTraversal creates a breadth first traversal string of 
	 * the letters in the strie.
	 * @return string of letters.
	 */
	public String levelOrderTraversal(){
		// Perform a Breadth First Traversal of your Strie tree
		// and return a string of all characters encountered in the traversal.
		// - If a Strie has no words, return an empty string.
		// - A single space should be padded between characters.
		// - For multiple children of a single node, use the order of characters in 
		// getKeys() of the hash map to determine the traverse order.
		//
		// Check main() for examples.
		
		// Hint: you can use SimpleList to implement a queue easily.
		
		// Note: While there are **no Big-O restrictions** on this
		// method, level order traversals are traditionally O(n)
		// where n is the number of nodes in the tree. This may not
		// be the case here due to the hash table implementation
		// of children.

		//Stringbuilder is started and root is set as current node.
		StringBuilder finish = new StringBuilder("");
		StrieNode current = this.root;

		//SimpleList for holding all the nodes is made.
		SimpleList<StrieNode> allNodes = new SimpleList<>();


		while (current != null) {
			//For ever node, their children and keys are collected.
			HashMap<Character, StrieNode> kids = current.getAllChildren();
		    SimpleList<Character> chars = kids.getKeys();
			   //   Each node is added in order to the SimpleList and their 
			   //   characters are appended to the stringbuilder.

		    for (Character ch: chars) {

		    	allNodes.addLast(kids.getValue(ch));
				finish.append(ch + " ");
		    }
			//Iteration.
			current = allNodes.removeFirst();
		}
		//The string is returned.
		return finish.toString().trim();
	}

	/**
	 * getStrieWords returns all the words in our Strie in a SimpleList of 
	 * Strings.
	 * @return wordList is our SimpleList of words.
	 */
	public SimpleList<String> getStrieWords(){
		// Return all words currently stored in Strie.
		// If Strie has no words, return null.
		//
		// When there are multiple characters to continue from one node,
		// use the order of characters in getKeys() of the hash map to 
		// determine the traverse order.
		//
		// Also, prefix-words come before words that they are prefixes of. 
		// For example, 'bar' comes before 'barn'. 
		//
		// Check main() for examples.
		
		// Hint: Consider using recursion in your implementation.
		// Hint: You can define recursive helper functions.
		
		// Note: There are **no Big-O restrictions** on this
		// method, but it **can** be done in the same runtime as
		// a walk of the tree.

		//Start the wordList and set the root as our starting point.
		SimpleList<String> wordList = new SimpleList<>();
		StrieNode current = this.root;
		//Null if no values inside.
		if (this.root.getNumChildren() == 0) {
			return null;
		}

		//Intitialize our root hashmap and SimpleLists of the first set of 
		//characters and StrieNodes accordingly.
		HashMap<Character, StrieNode> kids = current.getAllChildren();
		SimpleList<Character> chars = kids.getKeys();
		SimpleList<StrieNode> startNodes = new SimpleList<>();

		//Initialize our word and current character holders.
		String word;
		Character currentChar;

		//Input all character StrieNodes into startNodes.
		for (Character ch: chars) {
			startNodes.addLast(kids.getValue(ch));
		}

		//Track our words remaining.
		int wordsLeft = this.numWords;
		//Start iterating through our characters and nodes.
		currentChar = chars.removeFirst();
		current = startNodes.removeFirst();

		//For every word.
		while (wordsLeft != 0) {
			//Call our wordAssembler.
			word = wordAssembler(currentChar, current);
			if (word != "") {
				//If the word is valid and read, it's added and the remaining 
				//subtracted.
				wordList.addLast(word);
				wordsLeft--;

			}
			else {
				//If invalid word, it's gonna explode anyway.
				currentChar = chars.removeFirst();
				current = startNodes.removeFirst();
			}
		}

		//Clear our checking flags and return the list.
		this.flagClearer();
		return wordList;
	}

	/**
	 * wordAssembler pulls each of our words out of the Strie.
	 * @param ch is the given character.
	 * @param node is the given node.
	 * @return string of the word.
	 */
	private String wordAssembler(Character ch, StrieNode node) {
		//A blank is returned if either value is null.
		if (node == null || ch == null) {
			return "";
		}
		//We save the hashmap of the child nodes of the given one.
		HashMap<Character, StrieNode> kids = node.getAllChildren();

		//We make SimpleLists of all the children's charcacters and nodes.
		SimpleList<Character> kidChars = kids.getKeys();
		SimpleList<StrieNode> levelNodes = new SimpleList<>();

		//This fills in our StrieNode SimpleList with every StrieNodes nodes.
		for (Character letter: kidChars) {
			levelNodes.addLast(kids.getValue(letter));
		}

		//We initialize our node and character to work with.
		StrieNode current = node;
		Character currentChar = ch;

		//We make a catcher for dealing with recursive calls.
		String catcher;

		//We save the firstNode and firstChar so we can use them after clearing 
		//them.
		String firstChar = ch+ "";
		StrieNode firstNode = node;

		//We start our StringBuilder.
		StringBuilder word = new StringBuilder("");
		
		//For every node in our root.
		while (current != null) {
			
			//Runs if the node has a true endMarker.
			if (current.isEnd()) {

				//If the flag has been checked.
				if (current.checkFlag()) {
					//If it is not the end of the branch.
					if (current.getNumChildren() != 0) {

						//Check if there are more valid entries the next one over.
						catcher = wordAssembler(kidChars.removeFirst(), levelNodes.removeFirst());
						if (catcher == "") {

							return "";
						}
						else {
							//Returns the recursive character stringing.
							return currentChar + catcher;
						}
					}
					//Catches invalid word cases.
					return "";
				}
				else {
					//Proper end of a word. Set flag so we know we've been here.
					current.setFlag();
					word.append(currentChar);
					//Append the word and return the stringbuilder.
					return word.toString();
				}
			}
			
			//Append the first character to the stringbuilder
			word.append(currentChar);

			//Catch any chance the node or character has become null.
			if (current == null || currentChar == null) {

				return "";
			}	
			else {
				//For the remaining nodes.
				while (current != null) {
					//Iterate to the next node and character
					current = levelNodes.removeFirst();
		            currentChar = kidChars.removeFirst();

					   //Check for validity.
				    catcher = wordAssembler(currentChar, current);
				    if (catcher == "") {

						   //If nothing in branch, check next one.
					    current = levelNodes.removeFirst();
		                currentChar = kidChars.removeFirst();
			    	    while (current != null) {
				         	catcher = wordAssembler(currentChar, current);
					    	if (catcher != "") {
								
								   //Fill in StringBuilder with proper word parts.
					    		word.append(catcher);
								return word.toString();
					    	}

							      //Continue iterations.
				    	    current = levelNodes.removeFirst();
		                    currentChar = kidChars.removeFirst();
				        }
				    }
				    else {
						   //Append the valid word parts to the stringbuilder.
					    word.append(catcher);
				    }

					   //If we are working with the first character in the word.
				    if (firstChar.compareTo(word.toString()) == 0) {
				    	if (firstNode.isEnd()== false) {
						    continue;
				    	}
				    }
				    else {
						      //Return the full word.
				        return word.toString();
					}
				}

				
			}
			
		}

		//For catching invalid cases.
		return "";
	}

	/**
	 * flagClearer goes through each node and removes the flags.
	 */
	private void flagClearer() {
		//Starts with the root.
		StrieNode current = this.root;
		SimpleList<StrieNode> allNodes = new SimpleList<>();

		while (current != null) {
			//Collects every character and removes the flag from each of their 
			//nodes.
			HashMap<Character, StrieNode> kids = current.getAllChildren();
		    SimpleList<Character> chars = kids.getKeys();

		    for (Character ch: chars) {
		    	allNodes.addLast(kids.getValue(ch));
		    }
			current.unSetFlag();
			current = allNodes.removeFirst();
		}
	}



	//----------------------------------------------------
	// example testing code... make sure you pass all ...
	// and edit this as much as you want!
	//----------------------------------------------------

	/**
	 * main runs all the tests for our Strie.
	 * @param args is the command line arguements.
	 */
	public static void main(String[] args){
		Strie myStrie = new Strie();

		String fortnite = myStrie.levelOrderTraversal();
		
        System.out.println("LevelOrder:" + fortnite + ".\n");

		if(myStrie.numWords()==0 && myStrie.getRoot().getNumChildren() == 0)
			System.out.println("Yay 1");
			
		myStrie.insert("a");
		StrieNode myRoot = myStrie.getRoot();
		
		System.out.println("" + myStrie.numWords());
		if (myRoot.getChild('a').getNumChildren() == 0) {
			System.out.println("It true\n");
		}
		if (myRoot.containsChild('a')) {
			System.out.println("It truer\n");
		}
		if (myRoot.getChild('a').isEnd()) {
			System.out.println("It true true\n");
		}
		if (myStrie.numWords()==1) {
			System.out.println("It true true true\n");
		}

		if(myStrie.numWords()==1 && myRoot.getChild('a').getNumChildren() == 0 
			&& myRoot.getChild('a').isEnd() && myRoot.containsChild('a'))
			System.out.println("Yay 2");

		myStrie.insert("bat");
		myStrie.insert("bar");
		myStrie.insert("barn");
		myStrie.insert("cat");

		if(myStrie.contains("cat"))
			System.out.println("Yay 3");

 
		String res = myStrie.levelOrderTraversal();
		System.out.println(res);

		res = myStrie.levelOrderTraversal();
		String actualOut = "a b c a a t r t n";
		if(res.equals(actualOut))
			System.out.println("Yay 4");


		SimpleList<String> yourWords = myStrie.getStrieWords();
		String display = "[a,bat,bar,barn,cat]";
		if (yourWords.size()==5 && yourWords.toString().equals(display))
			System.out.println("Yay 5");
		System.out.println(yourWords.toString());
 

		if(myStrie.remove("cat") && !myStrie.contains("cat"))
			System.out.println("Yay 6");
		yourWords = myStrie.getStrieWords();
		System.out.println(yourWords.toString());
		if (myStrie.contains("cat")) {
			System.out.println("Contains is wrong!");
		}

		yourWords = myStrie.getStrieWords();
		display = "[a,bat,bar,barn]";
		if (yourWords.size()==4 && yourWords.toString().equals(display))
				System.out.println("Yay 7");
		System.out.println(yourWords.toString());

		if(myStrie.remove("bat") && !myStrie.contains("bat")) {
			System.out.println("Yay 8");
			yourWords = myStrie.getStrieWords();
			System.out.println(yourWords.toString());

		}
			
		Strie bubbajay = new Strie();
		bubbajay.insert("she");
		bubbajay.insert("shells");
		bubbajay.insert("sells");
		bubbajay.insert("sea");
		bubbajay.insert("by");
		bubbajay.insert("the");
		bubbajay.insert("shore");
		bubbajay.insert("aaa");
		bubbajay.insert("ab3");
			
		bubbajay.insert("add");
		bubbajay.insert("acl");
		yourWords = bubbajay.getStrieWords();
		// System.out.println(yourWords.toString());
		bubbajay.insert("but");
		bubbajay.insert("butter");
		bubbajay.insert("bull");
		yourWords = bubbajay.getStrieWords();
		// System.out.println("Still good");
		// System.out.println(yourWords.toString());

		bubbajay.insert("burner");
		bubbajay.insert("burning");
		bubbajay.insert("burmy");
			
		bubbajay.insert("brown");
		// System.out.println("We good");
		yourWords = bubbajay.getStrieWords();
		// System.out.println("Still good");
		System.out.println(yourWords.toString());

	}
}
