package interval_tree;


import interval_tree.Factory.QueryGenerator;
import sun.rmi.runtime.Log;


/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        long generatorStartTime = System.nanoTime();
        QueryGenerator.generateBatchOfQueries("query_batch.txt");
        long generatorEstimatedTime = System.nanoTime() - generatorStartTime;

//        Experiment exp = new Experiment(QueryGenerator.csvToSql("test_data.csv"));
        Experiment exp = new Experiment("query_batch.txt");

        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();

        System.out.println("-- Partial Compound Index --");
        exp.testPartialFPGrowth();
        String partialIndexInfo = Logger.getInstance().toString();

        runtime = Runtime.getRuntime();
        runtime.gc();
        long partialIndexMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L);

        Logger.getInstance().reset();
        System.out.println("-- Full Index --");
        exp.testFullFPGrowth();
        String fullIndexInfo = Logger.getInstance().toString();

        runtime = Runtime.getRuntime();
        runtime.gc();
        long fullIndexMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L);



        System.out.println("generatorStartTime: " + generatorEstimatedTime/ 1000000000.0);

        System.out.println("-- Full Index --");
        System.out.println(fullIndexInfo);
        System.out.println("Used memory is mb: " + fullIndexMemory);

        System.out.println("-- Partial Index --");
        System.out.println(partialIndexInfo);
        System.out.println("Used memory is mb: " + partialIndexMemory);
    }
}
