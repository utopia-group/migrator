package migrator.util;

import java.util.Collection;
import java.util.Set;

/**
 * Generic data structure for MultiMap.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface MultiMap<K, V> {

    /**
     * Put a key-value pair into the MultiMap.
     *
     * @param key   the key
     * @param value the value
     * @return {@code true} if this collection changed as a result of the call
     */
    public boolean put(K key, V value);

    /**
     * Put a collection of values for a key into the MultiMap.
     *
     * @param key    the key
     * @param values a collection of values
     * @return {@code true} if this collection changed as a result of the call
     */
    public boolean putAll(K key, Collection<V> values);

    /**
     * Find all values for the given key.
     *
     * @param key the key
     * @return a collection of values
     */
    public Collection<V> get(K key);

    /**
     * Check if the MultiMap is empty.
     *
     * @return {@code true} if empty
     */
    public boolean isEmpty();

    /**
     * Get all keys in the MultiMap.
     *
     * @return all keys as a set
     */
    public Set<K> keySet();

    /**
     * Check if the MultiMap contains the given key
     *
     * @param key the key
     * @return {@code true} if the MutliMap contains the key
     */
    public boolean containsKey(K key);

    /**
     * Check if the MultiMap contains the key-value pair
     *
     * @param key   the key
     * @param value the value
     * @return {@code true} if the MutliMap contains the key-value pair
     */
    public boolean contains(K key, V value);

    /**
     * Get the number of key-value pairs in the MultiMap.
     *
     * @return the number of key-value pairs
     */
    public int size();

}
