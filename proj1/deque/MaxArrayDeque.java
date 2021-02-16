package deque;

import java.util.Comparator;


public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comp;


    public MaxArrayDeque(Comparator<T> c) {
        comp = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        int maxDex = 0;
        for (int i = 0; i < size(); i += 1) {
            int cmp = comp.compare(get(i), get(maxDex));
            if (cmp > 0) {
                maxDex = i;
            }
        }
        return get(maxDex);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        int maxDex = 0;
        for (int i = 0; i < size(); i += 1) {
            int cmp = c.compare(get(i), get(maxDex));
            if (cmp > 0) {
                maxDex = i;
            }
        }
        return get(maxDex);
    }


}



