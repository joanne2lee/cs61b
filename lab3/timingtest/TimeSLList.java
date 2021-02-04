package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        int[] numbers = {1000,2000,4000,8000,16000,32000,64000,128000};
        int M = 10000;
        SLListsOfSizes(numbers,Ns,times,opCounts,M);
        printTimingTable(Ns, times, opCounts);

    }

    private static void SLListsOfSizes(int[] listSizes, AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts, int ops) {
        int i = 0;
        while (i < listSizes.length) {
            int size = listSizes[i];
            Ns.addLast(size);

            SLList<Integer> sllist = new SLList<>();
            while (sllist.size() < size) {
                sllist.addLast(0);
            }

            Stopwatch sw = new Stopwatch();
            for (int op_count = 0; op_count < ops; op_count += 1) {
                sllist.getLast();
            }
            double getlast_time = sw.elapsedTime();
            times.addLast(getlast_time);
            opCounts.addLast(ops);

            i += 1;
        }
    }

}
