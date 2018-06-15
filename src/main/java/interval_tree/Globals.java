package interval_tree;

/**
 * Created by Richard on 2018-06-08.
 */
public class Globals {
    public static int STORAGE_CAPACITY = 10000;
    public static double MINSUP = .01;

    /**
     * Test queries
     */
    public static String QUERY_BATCH_FILE = "query_data/query_batch.txt";
    public static int NR_OF_QUERIES = 5000;
    public static int NR_OF_DUPLICATES = 50;
    public static int NR_OF_COMPOSITE_COLUMNS = 2;

    /**
     * Subspace clustering
     */
    public static double IDEAL_COVERAGE = 0.0; // ToDo: Maybe not necessary?
    public static int NR_OF_CELLS = 500;
    public static double CLUSER_MINSUP = 0.1;
}
