package interval_tree;

//import com.lodborg.intervaltree.IntegerInterval;
//import com.lodborg.intervaltree.Interval;
//import com.lodborg.intervaltree.IntervalTree;
//import com.lodborg.intervaltree.Interval.Bounded;

import weka.estimators.UnivariateKernelEstimator;
import weka.estimators.UnivariateNormalEstimator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
        //start              //end

        argv.add("10");   argv.add("20");
        argv.add("15");   argv.add("18");
        argv.add("15");   argv.add("18");
        argv.add("15");   argv.add("18");
        argv.add("15");   argv.add("18");
        argv.add("15");   argv.add("18");
        argv.add("15");   argv.add("18");

        argv.add("17");   argv.add("24");
        argv.add("17");   argv.add("24");
        argv.add("17");   argv.add("24");
        argv.add("17");   argv.add("24");
        argv.add("17");   argv.add("24");
        argv.add("17");   argv.add("24");
        argv.add("17");   argv.add("24");
        argv.add("17");   argv.add("24");
        argv.add("17");   argv.add("24");
        argv.add("17");   argv.add("24");

        argv.add("30");   argv.add("40");


        argv.add("0");   argv.add("50");

        try {
            if (argv.size() < 2) {
                System.out.println("Please specify a set of instances.");
                return;
            }
            KernelEstimator newEst = new KernelEstimator(0.01);
            for (int i = 0; i < argv.size() - 3; i += 2) {
//                newEst.addValue(Double.valueOf(argv.get(i)),
//                        Double.valueOf(argv.get(i + 1)), 1.0);
                newEst.addRange(Integer.valueOf(argv.get(i)), Integer.valueOf(argv.get(i + 1)), 1.0);
            }
            System.out.println(newEst);

            double start = Double.valueOf(argv.get(argv.size() - 2));
            double finish = Double.valueOf(argv.get(argv.size() - 1));

            for (double current = start; current < finish;
                 current += 1) {
                double prob = newEst.getProbability(current);
//                System.out.println("Data: " + current + " "
//                        +  prob / 0.00001);
                System.out.print("Data: " + current + " : " + prob + " ");
                for (int k = 0; k < prob / 0.00001; k++) {
                    System.out.print("*");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // https://github.com/bnjmn/weka/blob/master/weka/src/main/java/weka/estimators/KernelEstimator.java
        // https://www.programcreek.com/java-api-examples/index.php?source_dir=Weka-for-Android-master/src/weka/classifiers/meta/RegressionByDiscretization.java#

        //univariateKernel();

        UnivariateKernelEstimator e = new UnivariateKernelEstimator();

        e.addValue(10, 1);
        e.addValue(11, 1);
        e.addValue(12, 1);
        e.addValue(13, 1);
        e.addValue(10, 1);
        e.addValue(11, 1);
        e.addValue(12, 1);
        e.addValue(13, 1);
        e.addValue(10, 1);
        e.addValue(11, 1);
        e.addValue(12, 1);
        e.addValue(13, 1);

        e.addValue(20, 1);
        e.addValue(20, 1);
        e.addValue(21, 1);
        e.addValue(23, 1);
        e.addValue(23, 1);
        e.addValue(25, 1);
        e.addValue(25, 1);


        // https://www.programcreek.com/java-api-examples/index.php?source_dir=Weka-for-Android-master/src/weka/classifiers/meta/RegressionByDiscretization.java#
        double[][] Intervals =  e.predictIntervals(0.8);

        for (int k = 0; k < Intervals.length; k++) {
            System.out.println("Left: " + Intervals[k][0] + "\t Right: " + Intervals[k][1]);
        }
    }


    public static void univariateKernel(){
        // Get random number generator initialized by system
        Random r = new Random();

        // Create density estimator
        UnivariateKernelEstimator e = new UnivariateKernelEstimator();

        // Output the density estimator
        System.out.println(e);

        // Monte Carlo integration
        double sum = 0;
        for (int i = 0; i < 1000; i++) {
            sum += Math.exp(e.logDensity(r.nextDouble() * 10.0 - 5.0));
        }
        System.out.println("Approximate integral: " + 10.0 * sum / 1000);

        // Add Gaussian values into it
        for (int i = 0; i < 1000; i++) {
            e.addValue(0.1 * r.nextGaussian() - 3, 1);
            e.addValue(r.nextGaussian() * 0.25, 3);
        }

        // Monte Carlo integration
        sum = 0;
        int points = 10000;
        for (int i = 0; i < points; i++) {
            double value = r.nextDouble() * 10.0 - 5.0;
            sum += Math.exp(e.logDensity(value));
        }
        System.out.println("Approximate integral: " + 10.0 * sum / points);

        // Check interval estimates
        double[][] Intervals = e.predictIntervals(0.9);

        System.out.println("Printing kernel intervals ---------------------");

        for (int k = 0; k < Intervals.length; k++) {
            System.out.println("Left: " + Intervals[k][0] + "\t Right: " + Intervals[k][1]);
        }

        System.out.println("Finished kernel printing intervals ---------------------");

        double Covered = 0;
        for (int i = 0; i < 1000; i++) {
            double val = -1;
            if (r.nextDouble() < 0.25) {
                val = 0.1 * r.nextGaussian() - 3.0;
            } else {
                val = r.nextGaussian() * 0.25;
            }
            for (int k = 0; k < Intervals.length; k++) {
                if (val >= Intervals[k][0] && val <= Intervals[k][1]) {
                    Covered++;
                    break;
                }
            }
        }
        System.out.println("Coverage at 0.9 level for kernel intervals: " + Covered / 1000);

        // Compare performance to normal estimator on normally distributed data
        UnivariateKernelEstimator eKernel = new UnivariateKernelEstimator();
        UnivariateNormalEstimator eNormal = new UnivariateNormalEstimator();

        for (int j = 1; j < 5; j++) {
            double numTrain = Math.pow(10, j);
            System.out.println("Number of training cases: " +
                    numTrain);

            // Add training cases
            for (int i = 0; i < numTrain; i++) {
                double val = r.nextGaussian() * 1.5 + 0.5;
                eKernel.addValue(val, 1);
                eNormal.addValue(val, 1);
            }

            // Monte Carlo integration
            sum = 0;
            points = 10000;
            for (int i = 0; i < points; i++) {
                double value = r.nextDouble() * 20.0 - 10.0;
                sum += Math.exp(eKernel.logDensity(value));
            }
            System.out.println("Approximate integral for kernel estimator: " + 20.0 * sum / points);

            // Evaluate estimators
            double loglikelihoodKernel = 0, loglikelihoodNormal = 0;
            for (int i = 0; i < 1000; i++) {
                double val = r.nextGaussian() * 1.5 + 0.5;
                loglikelihoodKernel += eKernel.logDensity(val);
                loglikelihoodNormal += eNormal.logDensity(val);
            }
            System.out.println("Loglikelihood for kernel estimator: " +
                    loglikelihoodKernel / 1000);
            System.out.println("Loglikelihood for normal estimator: " +
                    loglikelihoodNormal / 1000);

            // Check interval estimates
            double[][] kernelIntervals = eKernel.predictIntervals(0.95);
            double[][] normalIntervals = eNormal.predictIntervals(0.95);

            System.out.println("Printing kernel intervals ---------------------");

            for (int k = 0; k < kernelIntervals.length; k++) {
                System.out.println("Left: " + kernelIntervals[k][0] + "\t Right: " + kernelIntervals[k][1]);
            }

            System.out.println("Finished kernel printing intervals ---------------------");

            System.out.println("Printing normal intervals ---------------------");

            for (int k = 0; k < normalIntervals.length; k++) {
                System.out.println("Left: " + normalIntervals[k][0] + "\t Right: " + normalIntervals[k][1]);
            }

            System.out.println("Finished normal printing intervals ---------------------");

            double kernelCovered = 0;
            double normalCovered = 0;
            for (int i = 0; i < 1000; i++) {
                double val = r.nextGaussian() * 1.5 + 0.5;
                for (int k = 0; k < kernelIntervals.length; k++) {
                    if (val >= kernelIntervals[k][0] && val <= kernelIntervals[k][1]) {
                        kernelCovered++;
                        break;
                    }
                }
                for (int k = 0; k < normalIntervals.length; k++) {
                    if (val >= normalIntervals[k][0] && val <= normalIntervals[k][1]) {
                        normalCovered++;
                        break;
                    }
                }
            }
            System.out.println("Coverage at 0.95 level for kernel intervals: " + kernelCovered / 1000);
            System.out.println("Coverage at 0.95 level for normal intervals: " + normalCovered / 1000);

            kernelIntervals = eKernel.predictIntervals(0.8);
            normalIntervals = eNormal.predictIntervals(0.8);
            kernelCovered = 0;
            normalCovered = 0;
            for (int i = 0; i < 1000; i++) {
                double val = r.nextGaussian() * 1.5 + 0.5;
                for (int k = 0; k < kernelIntervals.length; k++) {
                    if (val >= kernelIntervals[k][0] && val <= kernelIntervals[k][1]) {
                        kernelCovered++;
                        break;
                    }
                }
                for (int k = 0; k < normalIntervals.length; k++) {
                    if (val >= normalIntervals[k][0] && val <= normalIntervals[k][1]) {
                        normalCovered++;
                        break;
                    }
                }
            }
            System.out.println("Coverage at 0.8 level for kernel intervals: " + kernelCovered / 1000);
            System.out.println("Coverage at 0.8 level for normal intervals: " + normalCovered / 1000);
        }
    }
}
