package deque;
import java.util.Comparator;

public class testComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer a, Integer b){
        return a - b;
    }
}