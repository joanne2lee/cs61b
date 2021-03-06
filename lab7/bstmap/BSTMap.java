package bstmap;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private Node root;
    private int size;

    private class Node {
        private K key;
        private V val;
        private Node left;
        private Node right;

        public Node(K key, V val) {
            this.key = key;
            this.val = val;
        }
    }

    public BSTMap() {
        root = null;
        size = 0;
    }


    @Override
    public void clear() {
        root = null;
        size = 0;
    }


    @Override
    public boolean containsKey(K key) {
        if (root == null) return false;
        if (key == null) throw new IllegalArgumentException("cannot contain null");
        return containsKeyHelper(root, key);
    }

    private boolean containsKeyHelper(Node n, K key) {
        if (n.key.equals(key)) {
            return true;
        }
        if (n.key.compareTo(key) > 0) {
            return containsKeyHelper(n.left, key);
        }
        if (n.key.compareTo(key) < 0) {
            return containsKeyHelper(n.right, key);
        }
        return false;
    }


    @Override
    public V get(K key) {
        if (root == null) return null;
        if (key == null) throw new IllegalArgumentException("cannot get null");
        return getHelper(root, key);
    }

    private V getHelper(Node n, K key) {
        if (n.key.equals(key)) {
            return n.val;
        }
        else if (n.key.compareTo(key) > 0) {
            return getHelper(n.left, key);
        }
        else if (n.key.compareTo(key) < 0) {
            return getHelper(n.right, key);
        }
        return null;
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public void put(K key, V value) {
        root = putHelper(key, value, root);
        size += 1;
    }

    private Node putHelper(K key, V value, Node n) {
        Node pNode = new Node(key, value);
        if (n == null) {
            return pNode;
        }
        else if (pNode.key.compareTo(n.key) > 0) {
            n.right = putHelper(key, value, n.right);
        }
        else if (pNode.key.compareTo(n.key) < 0) {
            n.left = putHelper(key, value, n.left);
        }
        return n;
    }


    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("unsupported operation");
    }


}