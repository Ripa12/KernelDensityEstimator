package interval_tree;


import interval_tree.Factory.QueryGenerator;
import interval_tree.Factory.TablePropertiesBuilder;
import interval_tree.Factory.TableBaseProperties;
import interval_tree.Logger.Logger;

import static interval_tree.Globals.*;


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
        TableBaseProperties tableCount = new TableBaseProperties(new String[]{"kegg"});
        TablePropertiesBuilder tpb = new TablePropertiesBuilder(tableCount);
        tpb.setAvgDenseClustes(MEAN_NR_OF_CLUSTERS).setDenseColumnProb(CLUSTER_PROBABILITY);
        QueryGenerator qg;// = tpb.build(DATA_SET, "kegg");


//        long generatorStartTime = System.nanoTime();
//        qg.setNrOfQueries(NR_OF_QUERIES)
//        .setAverageNrOfCompositeColumns(NR_OF_COMPOSITE_COLUMNS)
//        .setAverageNrOfDuplicates(NR_OF_DUPLICATES)
//        .generateBatchOfQueries(QUERY_BATCH_FILE);
//        long generatorEstimatedTime = System.nanoTime() - generatorStartTime;

        Experiment exp = new Experiment(QUERY_BATCH_FILE);
        Runtime runtime;

        int samples = NR_OF_SAMPLES;
        while(samples > 0) {
            qg = tpb.build(DATA_SET, "kegg");
//            qg.setNrOfQueries(NR_OF_QUERIES)
//                    .setAverageNrOfCompositeColumns(NR_OF_COMPOSITE_COLUMNS)
//                    .setAverageNrOfDuplicates(NR_OF_DUPLICATES)
//                    .generateBatchOfQueries(QUERY_BATCH_FILE);

            // Get the Java runtime
            runtime = Runtime.getRuntime();
            // Run the garbage collector
            runtime.gc();

            /**
             * Partial Indexes
             */

            System.out.println("-- Partial Compound Index --");
            exp.testPartialFPGrowth(tableCount);
            String partialIndexInfo = Logger.getInstance().toString();
            Logger.getInstance().dump("result.txt",
                    samples == NR_OF_SAMPLES ? "Experiment\n---Partial Indexes---" : "---Partial Indexes---",
                    samples != NR_OF_SAMPLES);

            runtime = Runtime.getRuntime();
            runtime.gc();
            long partialIndexMemory = (runtime.totalMemory() - runtime.freeMemory());// / (1024L * 1024L);

            runtime = Runtime.getRuntime();
            Logger.getInstance().reset();

            /**
             * Full Indexes
             */

            System.out.println("-- Full Index --");
            exp.testFullFPGrowth(tableCount);
            String fullIndexInfo = Logger.getInstance().toString();
            Logger.getInstance().dump("result.txt", "---Full Indexes---", true);

            runtime.gc();
            long fullIndexMemory = (runtime.totalMemory() - runtime.freeMemory());// / (1024L * 1024L);


//            System.out.println("generatorStartTime: " + generatorEstimatedTime / 1000000000.0);

//            System.out.println("-- Full Index --");
//            System.out.println(fullIndexInfo);
//            System.out.println("Used memory is mb: " + fullIndexMemory);
//
//            System.out.println("-- Partial Index --");
//            System.out.println(partialIndexInfo);
//            System.out.println("Used memory is mb: " + partialIndexMemory);

            samples--;
        }
    }

}
