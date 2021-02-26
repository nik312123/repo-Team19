package org.team19;

/**
 * Represents a key and its corresponding value or, alternatively, a pair of elements
 *
 * @param <K> The type corresponding to the key or first element
 * @param <V> The type corresponding to the value or second element
 */
public class Pair<K, V> {
    
    /**
     * The key or first element of the {@link Pair}
     */
    private final K key;
    
    /**
     * The value or second element of the {@link Pair}
     */
    private final V value;
    
    /**
     * Initializes a {@link Pair} with the given key-value pair or pair of elements
     *
     * @param key   The key or first element of the {@link Pair}
     * @param value The value or second element of the {@link Pair}
     */
    public Pair(final K key, final V value) {
        this.key = key;
        this.value = value;
    }
    
    /**
     * Returns the key or first element of the {@link Pair}
     *
     * @return The key or first element of the {@link Pair}
     */
    public K getKey() {
        return key;
    }
    
    /**
     * Returns the value or second element of the {@link Pair}
     *
     * @return The value or second element of the {@link Pair}
     */
    public V getValue() {
        return value;
    }
}
