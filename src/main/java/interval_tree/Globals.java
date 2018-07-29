package interval_tree;

/**
 * Created by Richard on 2018-06-08.
 */
public class Globals {
    public static final int STORAGE_CAPACITY = 40000;
    public static final double MIN_SUP = .01;

    public static final int NR_OF_SAMPLES = 1;

    /**
     * Test queries
     */
    public static final String QUERY_BATCH_FILE = "query_data/query_batch.sql";
    public static final int NR_OF_QUERIES = 20000;
    public static final int NR_OF_DUPLICATES = 2;
    public static final int NR_OF_COMPOSITE_COLUMNS = 9;


//    ValidatePartialFPTreeParser : 20.133409494
//    ExtractItem-sets : 0.141765303
//    PopulatePartialFPTreeParser : 8.166698905


//size of 9780
//    -- Full Index --
//            --- Estimated time ---
//    queryGenerationTime : 0.0
//    SupportCountParser : 1.082667511
//    InitializePartialFPTreeParser : 0.0
//    FinalOptimization : 0.0
//    FullParser : 0.709842619
//    ValidatePartialFPTreeParser : 0.0
//    ExtractItem-sets : 0.040365814
//    PopulatePartialFPTreeParser : 0.0
//
//    Used memory is mb: 1180496
//            -- Partial Index --
//            --- Estimated time ---
//    queryGenerationTime : 0.0
//    SupportCountParser : 0.66199183
//    InitializePartialFPTreeParser : 0.707182355
//    FinalOptimization : 0.0
//    FullParser : 0.0
//    ValidatePartialFPTreeParser : 2.289008244
//    ExtractItem-sets : 0.079220498
//    PopulatePartialFPTreeParser : 8.334224431
//
//    Used memory is mb: 1316440

//    size of 12361 6
//    -- Full Index --
//            --- Estimated time ---
//    queryGenerationTime : 0.0
//    SupportCountParser : 1.240682959
//    InitializePartialFPTreeParser : 0.0
//    FinalOptimization : 0.0
//    FullParser : 0.801743482
//    ValidatePartialFPTreeParser : 0.0
//    ExtractItem-sets : 0.042713034
//    PopulatePartialFPTreeParser : 0.0
//
//    Used memory is mb: 1179416
//            -- Partial Index --
//            --- Estimated time ---
//    queryGenerationTime : 0.0
//    SupportCountParser : 0.754444957
//    InitializePartialFPTreeParser : 0.833085558
//    FinalOptimization : 0.0
//    FullParser : 0.0
//    ValidatePartialFPTreeParser : 3.424844308
//    ExtractItem-sets : 0.107906654
//    PopulatePartialFPTreeParser : 11.630858974
//
//    Used memory is mb: 1317200


    /**
     * Table properties
     */
    public static final int MEAN_NR_OF_CLUSTERS = 1;
    public static final int CLUSTER_PROBABILITY = 100;
//    public static final String DATA_SET = "table_data/test_data.csv";
public static final String DATA_SET = "table_data/limit.csv";

    /**
     * Subspace clustering
     */
    public static final double IDEAL_COVERAGE = 0.0; // ToDo: Maybe not necessary?
    public static final int NR_OF_CELLS = 100;
    public static final double CLUSTER_MIN_SUP = 0.1;
}
