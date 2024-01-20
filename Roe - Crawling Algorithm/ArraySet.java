/**
 * @author Bryce Roe
 * @version 1.0
 */

/**
 * These are the imports for Collections, util, and io.
 */
import java.util.Collections;
import java.util.*;
import java.io.*;

/**
 * ArraySet uses a Comparable generic and implements Iterable to iterate through 
 * and maintain generic ArrayLists that are treated like sets.
 */
public class ArraySet<T extends Comparable<T>> implements Iterable<T> {
    /**
     * The arraySet is initialized.
     */
    private ArrayList<T> arraySet;

    /**
     * ArraySet instantiates the new arraySet.
     */
    public ArraySet() {
        this.arraySet = new ArrayList<>();
    }

    /**
     * size spits out the size of the arraySet.
     * @return the size
     */
    public int size() {
        return this.arraySet.size();
    }

    /**
     * asList takes the current arraySet and makes it a List instead of 
     * just an ArrayList and returns it.
     * @return out, the List.
     */
    public List<T> asList() {
        List<T> out = new ArrayList<>();
        out = this.arraySet;

        return out;
    }

    /**
     * contains does a binary search over the current arraySet for the provided 
     * query and returns true if the item is present and false if not.
     * @param query
     * @return true or false
     */
    public boolean contains(T query) {
        if (Collections.binarySearch(this.arraySet, query) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * add first checks if the given item is null and throws a RuntimeException if needed. 
     * Then it checks with a binary search if it is in the arraySet. If it's not present, the 
     * negative number returned is converted to a place in the actual arraySet of where it should 
     * belong and it is added accordingly if it is not at the end. If it is at the end, it is just 
     * added normally. True or false is returned after it is added or after it is found within already.
     * @param item
     * @return true or false.
     * @throws RuntimeException
     */
    public boolean add(T item) throws RuntimeException {
        if (item == null) {
            throw new RuntimeException("ArraySet does not support null items");
        }
        if (Collections.binarySearch(this.arraySet, item) <= -1) {
            int place = (Collections.binarySearch(this.arraySet, item) + 1) * -1;
            if (place > this.arraySet.size()) {
                this.arraySet.add(item);
            }
            else {
                this.arraySet.add(place, item);
            }
            return true;
        }
        return false;
    }

    /**
     * get checks with a binary search if the given query is in the current arraySet. 
     * If it is, the item is returned. If not, null is returned.
     * @param query
     * @return T query or null.
     */
    public T get(T query) {
        if (Collections.binarySearch(this.arraySet, query) >= 0) {
            return query;
        }
        return null;
    }

    /**
     * toString just converts the current arraySet to a string and returns it immediately.
     * @return the arraySet as a String
     */
    @Override
    public String toString() {
        return this.arraySet.toString();
    }

    /**
     * compareTo takes the two given objects and compares them, returning the 
     * number.
     * @param obj
     * @return the number of the comparison
     */
    public int compareTo(T obj) {
        return this.compareTo(obj);
    }

    /**
     * iterator creates a new ArrayIterator to iterate through the current arraySet.
     * @return a new ArrayIterator
     */
    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    /**
     * ArrayIterator is an inner class that implements Iterator and allows for iterating 
     * through the parts of an arraySet.
     */
    private class ArrayIterator implements Iterator<T> {
        /**
         * slot is started at 0 to cover the whole arraySet.
         */
        private int slot = 0;

        /**
         * hasNext returns true or false if the current slot is still within the arraySet 
         * or not.
         * @return true or false
         */
        @Override
        public boolean hasNext() {
            return slot < arraySet.size();
        }

        /**
         * next returns the next item in the arraySet if there are more items, and null 
         * if there is nothing left.
         * @return The next item or null
         */
        @Override
        public T next() {
            if (this.hasNext() == true) {
                return arraySet.get(slot++);
                
            }
            return null;
        }
    }
}
