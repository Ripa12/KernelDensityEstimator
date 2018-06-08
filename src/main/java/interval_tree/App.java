package interval_tree;


import interval_tree.Factory.QueryGenerator;
import interval_tree.Factory.TablePropertiesBuilder;
import interval_tree.FrequentPatternMining.SupportCount.TableCount;
import sun.rmi.runtime.Log;

import static interval_tree.Globals.MINSUP;
import static interval_tree.Globals.QUERY_BATCH_FILE;


/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        /**
         * Initiate tables
         **/
        TableCount tableCount = new TableCount(MINSUP, new String[]{"test"});
        TablePropertiesBuilder tpb = new TablePropertiesBuilder(tableCount);
        tpb.setAvgDenseClustes(1).setDenseColumnProb(70);
        QueryGenerator qg = tpb.build("test_data.csv", "test");



        long generatorStartTime = System.nanoTime();
        qg.setNrOfQueries(50);
        qg.setMaxDuplicates(10);
        qg.generateBatchOfQueries(QUERY_BATCH_FILE);
        long generatorEstimatedTime = System.nanoTime() - generatorStartTime;

        Experiment exp = new Experiment(QUERY_BATCH_FILE);

        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();

        System.out.println("-- Partial Compound Index --");
        exp.testPartialFPGrowth(tableCount);
        String partialIndexInfo = Logger.getInstance().toString();

        runtime = Runtime.getRuntime();
        runtime.gc();
        long partialIndexMemory = (runtime.totalMemory() - runtime.freeMemory());// / (1024L * 1024L);

        Logger.getInstance().reset();
        System.out.println("-- Full Index --");
        exp.testFullFPGrowth(tableCount);
        String fullIndexInfo = Logger.getInstance().toString();

        runtime = Runtime.getRuntime();
        runtime.gc();
        long fullIndexMemory = (runtime.totalMemory() - runtime.freeMemory());// / (1024L * 1024L);



        System.out.println("generatorStartTime: " + generatorEstimatedTime/ 1000000000.0);

        System.out.println("-- Full Index --");
        System.out.println(fullIndexInfo);
        System.out.println("Used memory is mb: " + fullIndexMemory);

        System.out.println("-- Partial Index --");
        System.out.println(partialIndexInfo);
        System.out.println("Used memory is mb: " + partialIndexMemory);
    }

}
