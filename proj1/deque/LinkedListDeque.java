package deque;

import jh61b.junit.In;

public class LinkedListDeque<T> {
    private IntNode sentinel;
    private int size;
    private IntNode last;

    private class IntNode {
        public T item;
        public IntNode prev;
        public IntNode next;

        public IntNode(T i, IntNode p, IntNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    public LinkedListDeque() {
        sentinel = new IntNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public LinkedListDeque(T x) {
        sentinel = new IntNode(null, null, null);
        size = 1;
        IntNode node = new IntNode(x, sentinel, sentinel);
        sentinel.next = node;
        last = node;
        sentinel.prev = last;
    }


    public void addFirst(T item) {
        sentinel.next = new IntNode(item, sentinel, sentinel.next);
        size += 1;
    }

    public void addLast(T item) {
        last.next = new IntNode(item, last, sentinel);
        last = last.next;
        size += 1;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        IntNode node = sentinel.next;
        while (node.item != null) {
            System.out.print(node.item + " ");
            node = node.next;
        }
        System.out.println("");
    }

    public T removeFirst() {
        if (sentinel.next != null) {
            IntNode first = sentinel.next;
            sentinel.next = first.next;
            return first.item;
        }
        return null;
    }

    public T removeLast() {
        if (size != 0) {
            T last_item = last.item;
            last = last.prev;
            return last_item;
        }
        return null;
    }

    public T get(int index) {
        IntNode node = sentinel.next;
        int i = 0;
        while (i < size()) {
            if (i == index) {
                return node.item;
            }
            node = node.next;
            i += 1;
        }
        return null;
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }
        private T getRecursiveHelper(int i, IntNode node) {
            if (i == 0) {
                return node.item;
            }
            return getRecursiveHelper(i - 1, node.next);
        }



}