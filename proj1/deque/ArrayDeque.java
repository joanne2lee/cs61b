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


    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        size += 1;
        nextFirst = oneLess(nextFirst);
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        size += 1;
        nextLast = oneMore(nextLast);
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
        for (int i = oneMore(nextFirst); i <= nextLast; nextFirst = oneMore(nextFirst)) {
            System.out.print(items[i] + " ");
        }
        System.out.println("");
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        int firstIndex = oneMore(nextFirst);
        T first = items[firstIndex];
        items[firstIndex] = null;
        size -= 1;
        nextFirst = firstIndex;
        return first;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        int lastIndex = oneLess(nextLast);
        T last = items[lastIndex];
        items[lastIndex] = null;
        size -= 1;
        nextLast = lastIndex;
        return last;
    }

    public T get(int index) {
        int i = oneMore(nextFirst);
        while (i <= items.length) {
            if (i == index) {
                return items[i];
            }
            nextFirst = oneMore(nextFirst);
        }
        return null;
    }




}