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

        UnivariateKernelEstimator e = new UnivariateKernelEstimator();
//        e.addValue((1000000) , 30000);
//        e.addValue((500000) , 100);
//        e.addValue((500001) , 30000);
//        e.addValue((500002) , 100);
//        e.addValue((50000) , 50);
//        e.addValue((-15000) , 50);
//        e.addValue((10000) , 50);
//        e.addValue((-1000000) , 50);
//
//        IntStream.range(55000, 90000).filter(x->x%100==0).mapToDouble(x->x).forEach(x->e.addValue(x, 100/100));
//        IntStream.range(50000, 60000).filter(x->x%100==0).mapToDouble(x->x).forEach(x->e.addValue(x, 100/100));
//        IntStream.range(57000, 59000).filter(x->x%100==0).mapToDouble(x->x).forEach(x->e.addValue(x, 100/100));
//        IntStream.range(40000, 55000).filter(x->x%100==0).mapToDouble(x->x).forEach(x->e.addValue(x, 100/100));

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
            Statements stats = CCJSqlParserUtil.parseStatements(QueryGenerator.generateBatchOfQueries());
            generatorEstimatedTime = System.nanoTime() - generatorStartTime;

            parseStartTime = System.nanoTime();
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

        kernelPrepStartTime = System.nanoTime();
        for (Map.Entry<String, MyIntervalTree> entry : intervalTrees.entrySet())
        {
            entry.getValue().iterate(e);
        }
        kernelPrepEstimatedTime = System.nanoTime() - kernelPrepStartTime;

        //e.addValue(-50000000, 99);

        kernelStartTime = System.nanoTime();
        double[][] Intervals =  e.predictIntervals(.75);
        kernelEstimatedTime = System.nanoTime() - kernelStartTime;

        for (int k = 0; k < Intervals.length; k++) {
            System.out.println("Left: " + (Intervals[k][0]) + "\t Right: " + (Intervals[k][1]));
        }

        System.out.println("generatorStartTime: " + generatorEstimatedTime/ 1000000000.0);
        System.out.println("parseStartTime: " + parseEstimatedTime/ 1000000000.0);
        System.out.println("kernelPrepStartTime: " + kernelPrepEstimatedTime/ 1000000000.0);
        System.out.println("kernelStartTime: " + kernelEstimatedTime/ 1000000000.0);
    }
}
