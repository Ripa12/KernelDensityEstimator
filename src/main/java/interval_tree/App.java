package interval_tree;

//import com.lodborg.intervaltree.IntegerInterval;
//import com.lodborg.intervaltree.Interval;
//import com.lodborg.intervaltree.IntervalTree;
//import com.lodborg.intervaltree.Interval.Bounded;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App
{

    public static void main( String[] args )
    {
        //https://github.com/lodborg/interval-tree/tree/master/src/main/java/com/lodborg/intervaltree

        // Instantiate a new tree
        IntervalTree<Integer> tree = new IntervalTree<>();


        // Add some intervals
        tree.add(new IntegerInterval(-8, 200, Interval.Bounded.CLOSED));
        tree.add(new IntegerInterval(5, 120, Interval.Bounded.CLOSED));
        tree.add(new IntegerInterval(-9, -1, Interval.Bounded.CLOSED));
        tree.add(new IntegerInterval(15, 72, Interval.Bounded.CLOSED));
        tree.add(new IntegerInterval(10, 50, Interval.Bounded.CLOSED));
        tree.add(new IntegerInterval(20, 67, Interval.Bounded.CLOSED));
        tree.add(new IntegerInterval(30, 30, Interval.Bounded.CLOSED));

        // ToDo: Add support for duplicate values(record frequency)
        tree.add(new IntegerInterval(30, 30, Interval.Bounded.CLOSED));
        tree.add(new IntegerInterval(30, 30, Interval.Bounded.CLOSED));

        List<Interval<Integer>> result = tree.query(30);
        System.out.println( "Size: " + result.size() );

        List<String> argv = new ArrayList<>();
        //              //

        argv.add("1");   argv.add("1");
        argv.add("1");   argv.add("1");
        argv.add("1");   argv.add("1");
        argv.add("1");   argv.add("1");
        argv.add("1");   argv.add("1");
        argv.add("1");   argv.add("1");
        argv.add("20");   argv.add("1");
        argv.add("20");   argv.add("1");
        argv.add("20");   argv.add("1");
        argv.add("20");   argv.add("1");

        argv.add("8003");   argv.add("1");
        argv.add("8005");   argv.add("1");
        argv.add("8000");   argv.add("1");

        argv.add("0");   argv.add("10000");

        try {
            if (argv.size() < 2) {
                System.out.println("Please specify a set of instances.");
                return;
            }
            KernelEstimator newEst = new KernelEstimator(0.01);
            for (int i = 0; i < argv.size() - 3; i += 2) {
                newEst.addValue(Double.valueOf(argv.get(i)),
                        Double.valueOf(argv.get(i + 1)));
            }
            System.out.println(newEst);

            double start = Double.valueOf(argv.get(argv.size() - 2));
            double finish = Double.valueOf(argv.get(argv.size() - 1));
            for (double current = start; current < finish;
                 current += (finish - start) / 10000) {
                System.out.println("Data: " + current + " "
                        + newEst.getProbability(current) * 10000);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
