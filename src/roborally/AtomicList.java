/*
 * AtomicSortedMap.java
 *
 */
package roborally;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Martin Groß
 */
public class AtomicList<T> implements Iterable<T> {

    private final List<T> list;
    private Random random;
    private final Class<T> type;

    public AtomicList(Class<T> type) {
        this(type, new Random());
    }

    public AtomicList(Class<T> type, Random random) {
        this.list = new LinkedList<T>();
        this.random = random;
        this.type = type;
    }

    public synchronized boolean add(T element) {
        return list.add(element);
    }

    public synchronized T chooseRandomly() {
        return list.get(random.nextInt(list.size()));
    }

    public synchronized void clear() {
        list.clear();
    }

    public synchronized boolean contains(T element) {
        return list.contains(element);
    }

    public synchronized T[] elements() {
        T[] result = (T[]) Array.newInstance(type, list.size());
        int index = 0;
        for (T element : list) {
            result[index++] = element;
        }
        return result;
    }

    public synchronized boolean isEmpty() {
        return list.isEmpty();
    }

    public synchronized Iterator<T> iterator() {
        return new ArrayIterator<T>(elements());
    }

    public synchronized void moveToFront(T element) {
        if (list.contains(element) && !list.isEmpty()) {
            while (!list.get(0).equals(element)) {
                list.add(list.remove(0));
            }
        }
    }

    public synchronized boolean remove(T element) {
        if (list.contains(element)) {
            list.remove(element);
            return true;
        } else {
            return false;
        }
    }

    public synchronized void shuffle() {
        for (int index = list.size(); index > 0; index--) {
            T object = list.remove(random.nextInt(index));
            list.add(object);
        }
    }

    public synchronized void setSeed(long seed) {
        random = new Random(seed);
    }

    public synchronized int size() {
        return list.size();
    }

    @Override
    public synchronized String toString() {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (T element : list) {
            if (first) {
                first = false;
            } else {
                result.append(" | ");
            }
            result.append(element.toString());
        }
        return result.toString();
    }

    public synchronized T getFirst() {
        return (list.isEmpty())? null : list.get(0);
    }
/*

    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public T get(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public T set(int index, T element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void add(int index, T element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public T remove(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/
}
