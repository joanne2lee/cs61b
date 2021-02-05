package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> noResize = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        noResize.addLast(4);
        buggy.addLast(4);
        noResize.addLast(5);
        buggy.addLast(5);
        noResize.addLast(6);
        buggy.addLast(6);

        assertEquals(noResize.removeLast(), buggy.removeLast());
        assertEquals(noResize.removeLast(), buggy.removeLast());
        assertEquals(noResize.removeLast(), buggy.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();


        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggy.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int buggySize = buggy.size();

            } else if (operationNumber == 2 && L.size() > 0) {
                // getLast
                int last = L.getLast();
                int buggyLast = buggy.getLast();
            } else if (operationNumber == 3 && L.size() > 0) {
                // removeLast
                L.removeLast();
                buggy.removeLast();
            }

        }
    }


}
