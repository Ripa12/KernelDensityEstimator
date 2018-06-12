package interval_tree;


import interval_tree.Factory.QueryGenerator;
import interval_tree.Factory.TablePropertiesBuilder;
import interval_tree.FrequentPatternMining.SupportCount.TableProperties;
import org.apache.commons.math3.random.RandomDataGenerator;
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
         * Initiate tables for partial-index
         **/
        TableProperties tableCount = new TableProperties(MINSUP, new String[]{"kegg"});
        TablePropertiesBuilder tpb = new TablePropertiesBuilder(tableCount);
        tpb.setAvgDenseClustes(1).setDenseColumnProb(70);
        QueryGenerator qg = tpb.build("table_data/test_data.csv", "kegg");



        long generatorStartTime = System.nanoTime();
        qg.setNrOfQueries(2000)
        .setRelativeSizeOfInterval(3)
        .setAverageNrOfCompositeColumns(2)
        .setAverageNrOfDuplicates(100)
        .generateBatchOfQueries(QUERY_BATCH_FILE);
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

        runtime = Runtime.getRuntime();

        /**
         * Initiate tables for full-index
         **/
        tableCount = new TableProperties(MINSUP, new String[]{"kegg"});
        tpb = new TablePropertiesBuilder(tableCount);
        tpb.setAvgDenseClustes(1).setDenseColumnProb(70);
        tpb.build("table_data/test_data.csv", "kegg");

        Logger.getInstance().reset();
        System.out.println("-- Full Index --");
        exp.testFullFPGrowth(tableCount);
        String fullIndexInfo = Logger.getInstance().toString();


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
