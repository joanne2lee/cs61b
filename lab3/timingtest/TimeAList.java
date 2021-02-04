package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }


    public static void timeAListConstruction() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        int[] numbers = {1000,2000,4000,8000,16000,32000,64000,128000};
        AListsOfSizes(numbers,Ns,times,opCounts);
        printTimingTable(Ns, times, opCounts);

    }

    private static void AListsOfSizes(int[] listSizes, AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        int i = 0;
        while (i < listSizes.length) {
            int size = listSizes[i];
            Ns.addLast(size);

            Stopwatch sw = new Stopwatch();
            AList<Integer> alist = new AList<>();
            int alist_opcount = 0;
            while (alist.size() < size) {
                alist.addLast(0);
                alist_opcount += 1;
            }
            double alist_time = sw.elapsedTime();
            times.addLast(alist_time);
            opCounts.addLast(alist_opcount);

            i += 1;
        }
    }

}
