package interval_tree;

/**
 * Created by Richard on 2018-06-08.
 */
public class Globals {
    public static int STORAGE_CAPACITY = 19000;
    public static double MIN_SUP = .01;

    /**
     * Test queries
     */
    public static String QUERY_BATCH_FILE = "query_data/query_batch.txt";
    public static int NR_OF_QUERIES = 20000;
    public static int NR_OF_DUPLICATES = 50;
    public static int NR_OF_COMPOSITE_COLUMNS = 3;


    /**
     * Table properties
     */
    public static int MEAN_NR_OF_CLUSTERS = 2;
    public static int DENSE_COLUMNS_PROBABILITY = 90;
    public static String TABLE_FILENAME = "table_data/test_data.csv";

    /**
     * Subspace clustering
     */
    public static double IDEAL_COVERAGE = 0.0; // ToDo: Maybe not necessary?
    public static int NR_OF_CELLS = 200;
    public static double CLUSTER_MIN_SUP = 0.1;
}
