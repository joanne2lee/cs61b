package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;


public class ArrayDequeTest {



    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();

        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);

            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                a.addFirst(randVal);
            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                a.addLast(randVal);
            } else if (operationNumber == 2) {
                // removeFirst
                a.removeFirst();
            } else if (operationNumber == 3) {
                // removeLast
                a.removeLast();
            } else if (operationNumber == 4) {
                // size
                int size = a.size();
            }

        }
    }

    @Test
    public void resizeTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(0);
        a.removeLast();
        a.addLast(2);
        a.removeLast();
        a.addLast(4);
        a.addFirst(5);
        a.get(0);
        a.addFirst(7);
        a.addFirst(8);
        a.addFirst(9);
        a.removeFirst();
        a.addFirst(11);
        a.addFirst(12);
        a.addFirst(13);
        a.get(6);
        a.addLast(15);
        a.addFirst(16);
        a.addLast(17);
        a.get(0);
        assertEquals(11,(int)a.get(3));
    }



}