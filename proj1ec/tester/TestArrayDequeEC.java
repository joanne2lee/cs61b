package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> std = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();

        String m = "\n";
        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);

            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 20);
                std.addFirst(randVal);
                sol.addFirst(randVal);
                m += "addFirst(" + randVal + ")\n";
                assertEquals(m, sol.get(0), std.get(0));

            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 20);
                std.addLast(randVal);
                sol.addLast(randVal);
                m += "addLast(" + randVal + ")\n";
                assertEquals(m, sol.get(0), std.get(0));

            } else if (operationNumber == 2 && std.size()!= 0 && sol.size()!= 0) {
                // removeFirst
                Integer stdF = std.removeFirst();
                Integer solF = sol.removeFirst();
                m += "removeFirst()\n";
                assertEquals(m, solF, stdF);

            } else if (operationNumber == 3 && std.size()!= 0 && sol.size()!= 0) {
                // removeLast
                Integer stdL = std.removeLast();
                Integer solL = sol.removeLast();
                m += "removeLast()\n";
                assertEquals(m, solL, stdL);

            }

        }


    }
}

