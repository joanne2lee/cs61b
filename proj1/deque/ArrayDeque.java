package deque;



public class ArrayDeque<T> {
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
        T[] a = (T[]) new Object[capacity];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
    }



    private int firstIndex(int i) {
        if (i - 1 < 0) {
            return items.length - 1;
        }
        return i - 1;
    }

    private int lastIndex(int i) {
        if (i + 1 > items.length - 1) {
            return 0;
        }
        return i + 1;
    }


    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        size += 1;
        nextFirst = firstIndex(nextFirst);
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        size += 1;
        nextLast = lastIndex(nextLast);
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
        for (int i = lastIndex(nextFirst); i <= nextLast; nextFirst = lastIndex(nextFirst)) {
            System.out.print(items[i] + " ");
        }
        System.out.println("");
    }

    public T removeFirst() {
        T first = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        nextFirst += 1;
        return first;
    }

    public T removeLast() {
        T last = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        nextLast -= 1;
        return last;
    }

    public T get(int index) {
        int i = lastIndex(nextFirst);
        while (i <= items.length) {
            if (i == index) {
                return items[i];
            }
            nextFirst = lastIndex(nextFirst);
        }
        return null;
    }




}