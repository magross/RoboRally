/*
 * AtomicSortedMap.java
 *
 */
package roborally;

import java.lang.reflect.Array;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Martin Groß
 */
public class AtomicSortedMap<V> {

    private final SortedMap<String, V> map;
    private final Class<V> valueType;

    public AtomicSortedMap(Class<V> valueType) {
        this.map = new TreeMap<String, V>();
        this.valueType = valueType;
    }

    public boolean contains(String key) {
        synchronized (map) {
            return map.containsKey(key);
        }
    }

    public V get(String key) {
        synchronized (map) {
            return map.get(key);
        }
    }

    public String[] listKeys() {
        synchronized (map) {
            String[] result = (String[]) Array.newInstance(String.class, map.size());
            int index = 0;
            for (String key : map.keySet()) {
                result[index++] = key;
            }
            return result;
        }
    }

    public V[] listValues() {
        synchronized (map) {
            V[] result = (V[]) Array.newInstance(valueType, map.size());
            int index = 0;
            for (String key : map.keySet()) {
                result[index++] = map.get(key);
            }
            return result;
        }
    }

    public int size() {
        synchronized (map) {
            return map.size();
        }
    }

    public boolean remove(String key) {
        synchronized (map) {
            if (key != null && map.containsKey(key)) {
                map.remove(key);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean set(String key, V value) {
        synchronized (map) {
            if (map.containsKey(key)) {
                return false;
            } else {
                map.put(key, value);
                return true;
            }
        }
    }
}
