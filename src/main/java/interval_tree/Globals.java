package interval_tree;

/**
 * Created by Richard on 2018-06-08.
 */
public class Globals {
    public static int STORAGE_CAPACITY = 60000;
    public static double MINSUP = .01;

    /**
     * Test queries
     */
    public static String QUERY_BATCH_FILE = "query_data/query_batch.txt";
    public static int NR_OF_QUERIES = 30000;
    public static int NR_OF_DUPLICATES = 600;
    public static int NR_OF_COMPOSITE_COLUMNS = 5;

    /**
     * Subspace clustering
     */
    public static double IDEAL_COVERAGE = 0.0; // ToDo: Maybe not necessary?
    public static int NR_OF_CELLS = 200;
    public static double CLUSER_MINSUP = 0.1;
}
