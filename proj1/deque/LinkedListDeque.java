package deque;

import jh61b.junit.In;

public class LinkedListDeque<T> {
    private IntNode sentinel;
    private int size;

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
        IntNode node = new IntNode(x, sentinel, sentinel);
        sentinel.next = node;
        sentinel.prev = node;
        size = 1;
    }


    public void addFirst(T item) {
        size += 1;
        if (size == 0) {
            IntNode first = new IntNode(item, sentinel, sentinel);
            sentinel.next = first;
            sentinel.prev = first;
        }
        else {
            IntNode first = new IntNode(item, sentinel, sentinel.next);
            first.next.prev = first;
            sentinel.next = first;
        }
    }

    public void addLast(T item) {
        size += 1;
        if (size == 0) {
            IntNode last = new IntNode(item, sentinel, sentinel);
            sentinel.next = last;
            sentinel.prev = last;
        }
        else {
            IntNode last = new IntNode(item, sentinel.prev, sentinel);
            last.prev.next = last;
            sentinel.prev = last;
        }
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
        if (size != 0) {
            T first_item = sentinel.next.item;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            size -= 1;
            return first_item;
        }
        return null;
    }

    public T removeLast() {
        if (size != 0) {
            T last_item = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size -= 1;
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