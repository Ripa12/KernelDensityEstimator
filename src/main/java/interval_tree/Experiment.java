package interval_tree;

import interval_tree.CandidateIndex.AbstractIndex;
import interval_tree.CandidateIndex.FullIndex;
import interval_tree.CandidateIndex.IIndex;
import interval_tree.CandidateIndex.PartialIndex;
import interval_tree.DBMS.PostgreSql;
import interval_tree.DataStructure.IntervalTree;
import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.KnapsackProblem.DynamicProgramming;
import interval_tree.SqlParser.FPTreeParser;
import interval_tree.SqlParser.GenericExpressionVisitor;
import interval_tree.SqlParser.IExpressionVisitor;
import interval_tree.SqlParser.SupportCountParser;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.sql.SQLException;
import java.util.*;

public class Experiment {
    //https://github.com/lodborg/interval-tree/tree/master/src/main/java/com/lodborg/intervaltree

    // https://github.com/bnjmn/weka/blob/master/weka/src/main/java/weka/estimators/KernelEstimator.java
    // https://www.programcreek.com/java-api-examples/index.php?source_dir=Weka-for-Android-master/src/weka/classifiers/meta/RegressionByDiscretization.java#


    /**
     * Measure time
     */
    private long generatorStartTime;
    private long parseStartTime;
    private long kernelPrepStartTime;
    private long kernelStartTime;

    private long generatorEstimatedTime;
    private long parseEstimatedTime;
    private long kernelPrepEstimatedTime;
    private long kernelEstimatedTime;

    /**
     * Batch of queries
     */
    private String queryBatch;

    /**
     * List of Interval trees for storing all intervals extracted from queries, one for each column
     */
    private Map<String, IntervalTree> intervalTrees;


    /**
     * Constructor
     */
    public Experiment(String batch){

        generatorStartTime = 0;
        parseStartTime = 0;
        kernelPrepStartTime = 0;
        kernelStartTime = 0;

        generatorEstimatedTime = 0;
        parseEstimatedTime = 0;
        kernelPrepEstimatedTime = 0;
        kernelEstimatedTime = 0;

        intervalTrees = new HashMap<>();

        // ToDo: Read all column names before-hand
        intervalTrees.put("A", new IntervalTree("A"));
        intervalTrees.put("B", new IntervalTree("B"));
        intervalTrees.put("C", new IntervalTree("C"));
        intervalTrees.put("D", new IntervalTree("D"));

        queryBatch = batch;
    }

    public String toString(){
        return String.format("parseStartTime: %f \nkernelPrepStartTime: %f \nkernelStartTime: %f",
                parseEstimatedTime/ 1000000000.0,
                kernelPrepEstimatedTime/ 1000000000.0,
                kernelEstimatedTime/ 1000000000.0);
    }

    public void testFPGrowth(){
        Map<String, Integer[]> supportCount = new HashMap<>();
        supportCount.put("A", new Integer[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE});
        supportCount.put("B", new Integer[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE});
        supportCount.put("C", new Integer[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE});
        supportCount.put("D", new Integer[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE});

        parseQueries(new SupportCountParser(supportCount));

        intervalTrees.get("A").setMinVal(supportCount.get("A")[1]);
        intervalTrees.get("A").setMaxVal(supportCount.get("A")[2]);
        intervalTrees.get("B").setMinVal(supportCount.get("B")[1]);
        intervalTrees.get("B").setMaxVal(supportCount.get("B")[2]);
        intervalTrees.get("C").setMinVal(supportCount.get("C")[1]);
        intervalTrees.get("C").setMaxVal(supportCount.get("C")[2]);
        intervalTrees.get("D").setMinVal(supportCount.get("D")[1]);
        intervalTrees.get("D").setMaxVal(supportCount.get("D")[2]);

        PartialFPTree fpTree = new PartialFPTree(supportCount);

        parseQueries(new FPTreeParser(supportCount, fpTree));

        fpTree.extractItemSets(.1);

        List<IIndex> indexList = fpTree.getPartialIndices();
        
        suggestPartialIndexes(indexList);
    }

    public void run(boolean enablePartialIdxs){
        List<AbstractIndex> indexList = new ArrayList<>();


        Map<String, Integer[]> supportCount = new HashMap<>();
        supportCount.put("A", new Integer[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE});
        supportCount.put("B", new Integer[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE});
        supportCount.put("C", new Integer[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE});
        supportCount.put("D", new Integer[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE});

        System.out.println("--- Mine Frequency ---");
        parseQueries(new SupportCountParser(supportCount));

        intervalTrees.get("A").setMinVal(supportCount.get("A")[1]);
        intervalTrees.get("A").setMaxVal(supportCount.get("A")[2]);
        intervalTrees.get("B").setMinVal(supportCount.get("B")[1]);
        intervalTrees.get("B").setMaxVal(supportCount.get("B")[2]);
        intervalTrees.get("C").setMinVal(supportCount.get("C")[1]);
        intervalTrees.get("C").setMaxVal(supportCount.get("C")[2]);
        intervalTrees.get("D").setMinVal(supportCount.get("D")[1]);
        intervalTrees.get("D").setMaxVal(supportCount.get("D")[2]);

        System.out.println("--- Mine Predicates ---");
        parseQueries(new GenericExpressionVisitor(intervalTrees));

        if(enablePartialIdxs)
            suggestPartialIndexes(indexList);
        else
            suggestFullIndexes(indexList);

        testIndexes(indexList, queryBatch);
    }


    private void parseQueries(IExpressionVisitor visitor){
//        ExpressionVisitor visitor = new GenericExpressionVisitor(intervalTrees); // ToDo: pass visitor as argument instead to allow for polymorphism

        //CCJSqlParserManager parserManager = new CCJSqlParserManager();

        Select select;
        try {
            parseStartTime = System.nanoTime();
            Statements stats = CCJSqlParserUtil.parseStatements(queryBatch); // ToDo: Insertion into interval trees might take longer time than the actual parsing of queries (try to fix if possible)...
            for(Statement statement : stats.getStatements()){
                select = (Select) statement;
                PlainSelect ps = (PlainSelect) select.getSelectBody();

                Expression exp = ps.getWhere();

                visitor.before();
                exp.accept(visitor);
                visitor.after();

                //System.out.println();
            }
            parseEstimatedTime = System.nanoTime() - parseStartTime;
        } catch (JSQLParserException e1) {
            e1.printStackTrace();
        }
    }

    private void suggestFullIndexes(List<AbstractIndex> indexList){
        for (Map.Entry<String, IntervalTree> entry : intervalTrees.entrySet())
        {
            kernelPrepStartTime = System.nanoTime();
            kernelPrepEstimatedTime += System.nanoTime() - kernelPrepStartTime;

            kernelStartTime = System.nanoTime();
            kernelEstimatedTime += System.nanoTime() - kernelStartTime;

            System.out.println("Column: " + entry.getKey() + "\tFrequency: " + entry.getValue().getFrequency());
            indexList.add(new FullIndex((double)entry.getValue().getFrequency(), 0, entry.getKey()));
        }
    }

    private void suggestPartialIndexes(List<AbstractIndex> indexList){
        kernelPrepStartTime = System.nanoTime();
        for (Map.Entry<String, IntervalTree> entry : intervalTrees.entrySet())
        {
            kernelPrepStartTime = System.nanoTime();
            entry.getValue().iterate(); // ToDo: combine iterate and predictInterval
            kernelPrepEstimatedTime += System.nanoTime() - kernelPrepStartTime;

            kernelStartTime = System.nanoTime();
            double[][] interval = entry.getValue().predictIntervals(.9);
            kernelEstimatedTime += System.nanoTime() - kernelStartTime;
            for (int p = 0; p < interval.length; p++) {
                System.out.println("Left: " + (interval[p][0]) + "\t Right: " + (interval[p][1]) + "\t Probability: " + (interval[p][2]));
                indexList.add(new PartialIndex(((double)entry.getValue().getFrequency() * (interval[p][2])), 0, entry.getKey(), (int)interval[p][0], (int)interval[p][1]));
            }
        }
    }

    // ToDo: Maybe pass a list of individual queries and not all queries in same string
    private void testIndexes(List<AbstractIndex> indexList, String queryBatch){
        PostgreSql postSql = null;
        try {
            postSql = new PostgreSql();
            postSql.estimateWeights(indexList);

            DynamicProgramming.solveKP(indexList, 13000000);

            postSql.buildCandidateIndexes(indexList);
            postSql.testIndexes(queryBatch);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        finally {
            try {
                postSql.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

}
