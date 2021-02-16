package deque;

import jh61b.junit.In;
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
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

    @Override
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

    @Override
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


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        IntNode node = sentinel.next;
        while (node.item != null) {
            System.out.print(node.item + " ");
            node = node.next;
        }
        System.out.println("");
    }

    @Override
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

    @Override
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

    @Override
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


    public Iterator<T> iterator() {
        return new LLDIterator();
    }


    private class LLDIterator implements Iterator<T> {
        private int index;

        public LLDIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public T next() {
            T item = get(index);
            index += 1;
            return item;
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        if (((LinkedListDeque<T>) o).size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i += 1) {
            if (!(this.get(i).equals(((LinkedListDeque<T>) o).get(i)))) {
                return false;
            }
        }
        return true;
    }

}