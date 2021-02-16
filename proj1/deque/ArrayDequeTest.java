package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;


public class ArrayDequeTest {



    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();

        int N = 5000;
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



}