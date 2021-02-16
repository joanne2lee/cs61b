package deque;


import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    private void resize(int capacity) {
        T[] newArray = (T[]) new Object[capacity];
        int newIndex = 0;
        int itemsIndex = (nextFirst + 1) % items.length;
        for (int n = 0; n < size; n += 1) {
            newArray[newIndex] = items[itemsIndex];
            newIndex += 1;
            itemsIndex = (itemsIndex + 1) % items.length;
        }
        items = newArray;
        nextFirst = items.length - 1;
        nextLast = size;
    }



    private int oneLess(int i) {
        if (i - 1 < 0) {
            return items.length - 1;
        }
        return i - 1;
    }

    private int oneMore(int i) {
        if (i + 1 > items.length - 1) {
            return 0;
        }
        return i + 1;
    }


    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        size += 1;
        nextFirst = oneLess(nextFirst);
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        size += 1;
        nextLast = oneMore(nextLast);
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = (nextFirst + 1) % items.length; i < nextLast; i = (i + 1) % items.length) {
            System.out.print(items[i] + " ");
        }
        System.out.println("");
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        int firstIndex = oneMore(nextFirst);
        T first = items[firstIndex];
        items[firstIndex] = null;
        size -= 1;
        nextFirst = firstIndex;
        if (items.length >= 16 && size < items.length / 4) {
            resize(items.length / 2);
        }
        return first;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        int lastIndex = oneLess(nextLast);
        T last = items[lastIndex];
        items[lastIndex] = null;
        size -= 1;
        nextLast = lastIndex;
        if (items.length >= 16 && size < items.length / 4) {
            resize(items.length / 2);
        }
        return last;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int gIndex = (nextFirst + 1 + index) % items.length;
        return items[gIndex];
    }


    public Iterator<T> iterator() {
        return new ADIterator();
    }

    private class ADIterator implements Iterator<T> {
        private int index;

        ADIterator() {
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
        if (!(o instanceof Deque)) {
            return false;
        }
        if (((Deque<T>) o).size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i += 1) {
            if (!(this.get(i).equals(((Deque<T>) o).get(i)))) {
                return false;
            }
        }
        return true;
    }


}



