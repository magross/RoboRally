/*
 * ArrayIterator.java
 *
 */

package roborally;

import java.util.Iterator;

/**
 *
 * @author Martin Groß
 */
public class ArrayIterator<T> implements Iterator<T> {

    private T[] array;
    private int position;

    public ArrayIterator(T[] array) {
        this.array = array;
        this.position = 0;
    }

    public boolean hasNext() {
        return position < array.length;
    }

    public T next() {
        return array[position++];
    }

    public void remove() {
        throw new UnsupportedOperationException("This is not supported.");
    }
}
