package interval_tree;

//import com.lodborg.intervaltree.IntegerInterval;
//import com.lodborg.intervaltree.Interval;
//import com.lodborg.intervaltree.IntervalTree;
//import com.lodborg.intervaltree.Interval.Bounded;


        import net.sf.jsqlparser.JSQLParserException;
        import net.sf.jsqlparser.expression.Expression;
        import net.sf.jsqlparser.expression.ExpressionVisitor;
        import net.sf.jsqlparser.parser.CCJSqlParserManager;
        import net.sf.jsqlparser.parser.CCJSqlParserUtil;
        import net.sf.jsqlparser.statement.Statement;
        import net.sf.jsqlparser.statement.Statements;
        import net.sf.jsqlparser.statement.select.FromItem;
        import net.sf.jsqlparser.statement.select.PlainSelect;
        import net.sf.jsqlparser.statement.select.Select;

        import java.sql.SQLException;
        import java.util.*;
        import java.util.stream.IntStream;

/**
 * Hello world!
 *
 */
public class App
{

    public static void main( String[] args )
    {
        //https://github.com/lodborg/interval-tree/tree/master/src/main/java/com/lodborg/intervaltree

        // https://github.com/bnjmn/weka/blob/master/weka/src/main/java/weka/estimators/KernelEstimator.java
        // https://www.programcreek.com/java-api-examples/index.php?source_dir=Weka-for-Android-master/src/weka/classifiers/meta/RegressionByDiscretization.java#


        Map<String, MyIntervalTree> intervalTrees = new HashMap<String, MyIntervalTree>();
        long generatorStartTime = 0;
        long parseStartTime;
        long kernelPrepStartTime;
        long kernelStartTime;

        long generatorEstimatedTime = 0;
        long parseEstimatedTime = 0;
        long kernelPrepEstimatedTime = 0;
        long kernelEstimatedTime = 0;

        // ToDo: Read all column names before-hand
        intervalTrees.put("A", new MyIntervalTree());
        intervalTrees.put("B", new MyIntervalTree());
        intervalTrees.put("C", new MyIntervalTree());
        ExpressionVisitor visitor = new GenericExpressionVisitor(intervalTrees);

        CCJSqlParserManager parserManager = new CCJSqlParserManager();

        Select select = null;
        try {
            generatorStartTime = System.nanoTime();
            String queryBatch = QueryGenerator.generateBatchOfQueries();
            generatorEstimatedTime = System.nanoTime() - generatorStartTime;


            parseStartTime = System.nanoTime();
            Statements stats = CCJSqlParserUtil.parseStatements(queryBatch); // ToDo: Insertion into interval trees might take longer time than the actual parsing of queries (try to fix if possible)...
            for(Statement statement : stats.getStatements()){
                select = (Select) statement;
                PlainSelect ps = (PlainSelect) select.getSelectBody();

                Expression exp = ps.getWhere();

                exp.accept(visitor);
                System.out.println();
            }
            parseEstimatedTime = System.nanoTime() - parseStartTime;
        } catch (JSQLParserException e1) {
            e1.printStackTrace();
        }

        //UnivariateKernelEstimator[] e = new UnivariateKernelEstimator[QueryGenerator.COLUMNS.length];

        int counter = 0;
        List<CandidateIndex> indexList = new ArrayList<>();

//        kernelPrepStartTime = System.nanoTime();
        for (Map.Entry<String, MyIntervalTree> entry : intervalTrees.entrySet())
        {
            //e[counter] = new UnivariateKernelEstimator();
            kernelPrepStartTime = System.nanoTime();
            entry.getValue().iterate(); // ToDo: combine iterate and predictInterval
            kernelPrepEstimatedTime += System.nanoTime() - kernelPrepStartTime;

//            counter++;

            kernelStartTime = System.nanoTime();
            double[][] interval = entry.getValue().predictIntervals(.85);
            kernelEstimatedTime += System.nanoTime() - kernelStartTime;
            for (int p = 0; p < interval.length; p++) {
                System.out.println("Left: " + (interval[p][0]) + "\t Right: " + (interval[p][1]) + "\t Probability: " + (interval[p][2]));
                indexList.add(new PartialIndex(((double)entry.getValue().getFrequency() * (interval[p][2])), 0, entry.getKey(), (int)interval[p][0], (int)interval[p][1]));
            }

        }

      PostgreSql postSql = new PostgreSql();
        try {
            postSql.estimateWeights(indexList);
        } catch (SQLException e) {
            e.printStackTrace();
        }


//        kernelPrepEstimatedTime = System.nanoTime() - kernelPrepStartTime;

//        double[][][] intervals = new double[QueryGenerator.COLUMNS.length][][];

//        kernelStartTime = System.nanoTime();
//        for(int i = 0; i < counter; i++) {
//            intervals[i] = e[i].predictIntervals(.85);
//        }
//        kernelEstimatedTime = System.nanoTime() - kernelStartTime;



//        for (int p = 0; p < intervals.length; p++) {
//            System.out.println("--- " + p + " ---");
//            for (int k = 0; k < intervals[p].length; k++) {
//                System.out.println("Left: " + (intervals[p][k][0]) + "\t Right: " + (intervals[p][k][1]) + "\t Probability: " + (intervals[p][k][2]));
//                indexList.add(new PartialIndex(0, 0, null, 6, 6));
//            }
//        }

        System.out.println("generatorStartTime: " + generatorEstimatedTime/ 1000000000.0);
        System.out.println("parseStartTime: " + parseEstimatedTime/ 1000000000.0);
        System.out.println("kernelPrepStartTime: " + kernelPrepEstimatedTime/ 1000000000.0);
        System.out.println("kernelStartTime: " + kernelEstimatedTime/ 1000000000.0);
    }
}
