package hashmap;


import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size = 0;

    private int initSize = 16;
    private double maxLoadFactor = .75;

    private HashSet<K> keys = new HashSet<>();

    /** Constructors */
    public MyHashMap() {
        buckets = new Collection[initSize];
    }

    public MyHashMap(int initialSize) {
        this.initSize = initialSize;
        buckets = new Collection[initSize];
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initSize = initialSize;
        this.maxLoadFactor = maxLoad;
        buckets = new Collection[initialSize];

    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return null;
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }

    @Override
    public void clear() {
        buckets = new Collection[initSize];
        keys = new HashSet<>();
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return keys.contains(key);
    }

    @Override
    public V get(K key) {
        if (containsKey(key)) {
            Iterator<Node> bucket = buckets[bucketIndex(key)].iterator();
            while (bucket.hasNext()) {
                Node n = bucket.next();
                if (n.key.equals(key)) {
                    return n.value;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return this.size;
    }


    @Override
    public void put(K key, V value) {

        double loadFactor = size / buckets.length;
        if (loadFactor >= maxLoadFactor) {
            resize(buckets.length * 2);
        }

        int i = bucketIndex(key);

        if (containsKey(key)) {
            Iterator<Node> bucket = buckets[i].iterator();
            while (bucket.hasNext()) {
                Node n = bucket.next();
                if (n.key.equals(key)) {
                    n.value = value;
                }
            }
        }

        else {
            if (buckets[i] == null) {
                buckets[i] = createBucket();
            }
            Node n = new Node(key, value);
            buckets[i].add(n);
            keys.add(key);
            size += 1;
        }
    }

    private int bucketIndex(K key) {
        int keyHash = key.hashCode();
        int keyIndex = Math.floorMod(keyHash, buckets.length);
        return keyIndex;
    }



    private void resize(int newSize) {
        MyHashMap<K, V> temp = new MyHashMap<>(newSize);;
        for (K k : keySet()) {
            temp.put(k, get(k));
        }
        this.buckets = temp.buckets;
    }



    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public Iterator<K> iterator() {
        return keys.iterator();
    }


    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("");
    }


    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("");
    }

}


