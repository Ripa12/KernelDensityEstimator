package interval_tree;


import interval_tree.Factory.QueryGenerator;
import interval_tree.Factory.TablePropertiesBuilder;
import interval_tree.Factory.TableBaseProperties;
import interval_tree.Logger.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static interval_tree.Globals.*;


/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {

        outputToCSV("limit.csv", 100); // ToDo: Debug only

        /**
         * Initiate tables for partial-index
         **/
        TableBaseProperties tableCount = new TableBaseProperties(new String[]{"kegg"});
        TablePropertiesBuilder tpb = new TablePropertiesBuilder(tableCount);
        tpb.setAvgDenseClustes(MEAN_NR_OF_CLUSTERS).setDenseColumnProb(CLUSTER_PROBABILITY);
        QueryGenerator qg;

        Experiment exp = new Experiment(QUERY_BATCH_FILE);
        Runtime runtime;

        int samples = NR_OF_SAMPLES;
        while(samples > 0) {
            qg = tpb.build(DATA_SET, "kegg");
            qg.setNrOfQueries(NR_OF_QUERIES)
                    .setAverageNrOfCompositeColumns(NR_OF_COMPOSITE_COLUMNS)
                    .setAverageNrOfDuplicates(NR_OF_DUPLICATES)
                    .generateBatchOfQueries(QUERY_BATCH_FILE);

            // Get the Java runtime
            runtime = Runtime.getRuntime();
            // Run the garbage collector
            runtime.gc();

            /**
             * Full Indexes
             */

            System.out.println("-- Full Index --");
            exp.testFullFPGrowth(tableCount);
            String fullIndexInfo = Logger.getInstance().toString();
            Logger.getInstance().dump("result.txt", "---Full Indexes---", true);

            runtime.gc();
            long fullIndexMemory = (runtime.totalMemory() - runtime.freeMemory());// / (1024L * 1024L);

            Logger.getInstance().reset();

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


//            System.out.println("generatorStartTime: " + generatorEstimatedTime / 1000000000.0);

            System.out.println("-- Full Index --");
            System.out.println(fullIndexInfo);
            System.out.println("Used memory is mb: " + fullIndexMemory);

            System.out.println("-- Partial Index --");
            System.out.println(partialIndexInfo);
            System.out.println("Used memory is mb: " + partialIndexMemory);

            samples--;
        }
    }

    public static void outputToCSV(String filename, int size) {
        String targetPath = "data/testdata/unittests/table_data/" + filename;
        if (SystemUtils.IS_OS_WINDOWS) {
            targetPath = targetPath.replaceFirst("/", "//");
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(
                    new BufferedOutputStream(new FileOutputStream(targetPath)), "UTF-8"));

            for (int i = 0; i < size - 1; i++) {
                out.print("a" + i + ",");
            }
            out.println("a" + (size - 1));

            for (int i = 0; i < size - 1; i++) {
                out.print(1 + ",");
            }
            out.println(1);

            for (int i = 0; i < size - 1; i++) {
                out.print(100 + ",");
            }
            out.println(100);

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

}
