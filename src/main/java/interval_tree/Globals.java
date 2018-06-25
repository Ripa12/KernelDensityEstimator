package interval_tree;

/**
 * Created by Richard on 2018-06-08.
 */
public class Globals {
    public static final int STORAGE_CAPACITY = 25000;
    public static final double MIN_SUP = .01;

    public static final int NR_OF_SAMPLES = 7;

    /**
     * Test queries
     */
    public static final String QUERY_BATCH_FILE = "query_data/query_batch.sql";
    public static final int NR_OF_QUERIES = 20000;
    public static final int NR_OF_DUPLICATES = 300;
    public static final int NR_OF_COMPOSITE_COLUMNS = 3;


    /**
     * Table properties
     */
    public static final int MEAN_NR_OF_CLUSTERS = 1;
    public static final int CLUSTER_PROBABILITY = 10;
    public static final String DATA_SET = "table_data/test_data.csv";

    /**
     * Subspace clustering
     */
    public static final double IDEAL_COVERAGE = 0.0; // ToDo: Maybe not necessary?
    public static final int NR_OF_CELLS = 200;
    public static final double CLUSTER_MIN_SUP = 0.3;
}
