package Indexer;


import Indexer.Factory.QueryGenerator;
import Indexer.Factory.TablePropertiesBuilder;
import Indexer.Factory.TableBaseProperties;
import Indexer.GUI.Menu;
import Indexer.Logger.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.net.URLDecoder;

import static Indexer.Utility.getOutputDirectory;


/**
 * Hello world!
 */
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static void run() {
        assert menu != null;

        outputToCSV("limit.csv", 20); // ToDo: Debug only

        /**
         * Initiate tables for partial-index
         **/
        TableBaseProperties tableCount = new TableBaseProperties(new String[]{"kegg"});
        TablePropertiesBuilder tpb = new TablePropertiesBuilder(tableCount);
        tpb.setAvgDenseClustes(menu.getIntegerProperty("MEAN_NR_OF_CLUSTERS"))
                .setDenseColumnProb(menu.getIntegerProperty("CLUSTER_PROBABILITY"));
        QueryGenerator qg;

        Experiment exp = new Experiment(menu, menu.getStringProperty("QUERY_BATCH_FILE"));
        Runtime runtime;

        int samples = menu.getIntegerProperty("NR_OF_SAMPLES");
        int counter = 0;
        while (counter < samples) {
            qg = tpb.build(menu.getStringProperty("DATA_SET"), "kegg");
            qg.setNrOfQueries(menu.getIntegerProperty("NR_OF_QUERIES"))
                    .setAverageNrOfCompositeColumns(menu.getIntegerProperty("NR_OF_COMPOSITE_COLUMNS"))
                    .setAverageNrOfDuplicates(menu.getIntegerProperty("NR_OF_DUPLICATES"))
                    .generateBatchOfQueries(menu.getStringProperty("QUERY_BATCH_FILE"));

            // Get the Java runtime
            runtime = Runtime.getRuntime();
            // Run the garbage collector
            runtime.gc();


            String filename = menu.getStringProperty("OUTCOME");
            if (SystemUtils.IS_OS_WINDOWS) {
                filename = filename.replaceFirst("/", "//");
            }

            File file = new File(getOutputDirectory() + filename);

            System.out.println("\nSample " + (counter + 1) + "\n");

            /**
             * Former Indexer
             */
            exp.testFullFPGrowth(tableCount);
            String fullIndexInfo = Logger.getInstance().toString();
            Logger.getInstance().dump(file, "\nSample " + (counter + 1) + "\n---Former Indexer---", counter != 0);

            runtime.gc();
            long fullIndexMemory = (runtime.totalMemory() - runtime.freeMemory());// / (1024L * 1024L);

            Logger.getInstance().reset();

            /**
             * Extended Indexer
             */

            exp.testPartialFPGrowth(tableCount);
            String partialIndexInfo = Logger.getInstance().toString();
            Logger.getInstance().dump(file, "---Extended Indexer---", true);

            runtime = Runtime.getRuntime();
            runtime.gc();
            long partialIndexMemory = (runtime.totalMemory() - runtime.freeMemory());// / (1024L * 1024L);

//            System.out.println("generatorStartTime: " + generatorEstimatedTime / 1000000000.0);

            System.out.println("-- Former Indexer --");
            System.out.print(fullIndexInfo);
            System.out.println("Used memory is mb: " + fullIndexMemory);

            System.out.println("-- Extended Indexer --");
            System.out.print(partialIndexInfo);
            System.out.println("Used memory is mb: " + partialIndexMemory);

            counter++;
        }
    }

    public static void outputToCSV(String filename, int size) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(
                    new BufferedOutputStream(new FileOutputStream(getOutputDirectory() + filename)), "UTF-8"));

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

    private static Menu menu;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Indexer");

        menu = new Menu(event -> run());

        primaryStage.setOnCloseRequest(event -> menu.saveProperties());

        primaryStage.setScene(new Scene(menu, 500, 660));
        primaryStage.show();
    }
}
