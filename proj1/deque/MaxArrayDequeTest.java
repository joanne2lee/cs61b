package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;


public class MaxArrayDequeTest {


    @Test
    public void maxTest() {
        TestComparator c = new TestComparator();
        MaxArrayDeque a = new MaxArrayDeque(c);
        a.addLast(5);
        a.addLast(10);
        a.removeLast();
        a.addLast(7);
        a.addLast(7);
        assertEquals(7, a.max());

    }


}