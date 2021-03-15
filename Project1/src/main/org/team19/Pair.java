/*
 * File name:
 * Pair.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Represents a key and its corresponding value or, alternatively, a pair of elements
 */

package org.team19;

import java.util.Objects;

/**
 * Represents a key and its corresponding value or, alternatively, a pair of elements
 *
 * @param <K> The type corresponding to the key or first element
 * @param <V> The type corresponding to the value or second element
 */
public final class Pair<K, V> {
    
    /**
     * The key or first element of the {@link Pair}
     */
    private final K key;
    
    /**
     * The value or second element of the {@link Pair}
     */
    private V value;
    
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
     * Returns the key or first element of the {@link Pair}
     *
     * @return The key or first element of the {@link Pair}
     */
    public K getFirst() {
        return getKey();
    }
    
    /**
     * Returns the value or second element of the {@link Pair}
     *
     * @return The value or second element of the {@link Pair}
     */
    public V getValue() {
        return value;
    }
    
    /**
     * Returns the value or second element of the {@link Pair}
     *
     * @return The value or second element of the {@link Pair}
     */
    public V getSecond() {
        return getValue();
    }
    
    /**
     * Sets the value or second element of this {@link Pair} to the provided value
     *
     * @param value The value to set for this {@link Pair}
     */
    public void setValue(final V value) {
        this.value = value;
    }
    
    /**
     * Sets the value or second element of this {@link Pair} to the provided value
     *
     * @param value The value to set for this {@link Pair}
     */
    public void setSecond(final V value) {
        setValue(value);
    }
    
    /**
     * Returns the string form of the {@link Pair}
     *
     * @return The string form of the {@link Pair}
     */
    @Override
    public String toString() {
        return String.format("Pair{%s, %s}", key, value);
    }
    
    /**
     * Returns true if the provided object is equivalent to this {@link Pair}
     *
     * @param other The object to compare to this {@link Pair}
     * @return True if the provided object is equivalent to this {@link Pair}
     */
    @Override
    public boolean equals(final Object other) {
        if(this == other) {
            return true;
        }
        if(!(other instanceof Pair)) {
            return false;
        }
        final Pair<?, ?> pair = (Pair<?, ?>) other;
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }
    
    /**
     * Returns the hashcode for this {@link Pair}
     *
     * @return The hashcode for this {@link Pair}
     */
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
