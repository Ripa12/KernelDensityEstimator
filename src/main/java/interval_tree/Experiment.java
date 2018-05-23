package interval_tree;

import interval_tree.CandidateIndex.*;
import interval_tree.DBMS.PostgreSql;
import interval_tree.DataStructure.IntervalTree;
import interval_tree.FrequentPatternMining.FullFPTree;
import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.FrequentPatternMining.SupportCount.ColumnCount;
import interval_tree.KnapsackProblem.DynamicProgramming;
import interval_tree.SqlParser.*;
import interval_tree.SqlParser.FullParser.FullParser;
import interval_tree.SqlParser.PartialParser.PopulateFPTreeParser;
import interval_tree.SqlParser.PartialParser.InitializeFPTreeParser;
import interval_tree.SqlParser.PartialParser.ValidateFPTreeParser;
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


    private static double MINSUP = .05;


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

        Logger.getInstance()
                .addTimer("kernelLoadTime")
                .addTimer("kernelRunTime")
                .addTimer("queryGenerationTime").addTimer("parseTime");


        intervalTrees = new HashMap<>();

        // ToDo: Read all column names before-hand
        intervalTrees.put("A", new IntervalTree("A"));
        intervalTrees.put("B", new IntervalTree("B"));
        intervalTrees.put("C", new IntervalTree("C"));
        intervalTrees.put("D", new IntervalTree("D"));

        queryBatch = batch;
    }

    public final void setIntervalMinMax(Map<String, Integer[]> supportCount){
        intervalTrees.get("A").setMinVal(supportCount.get("A")[1]);
        intervalTrees.get("A").setMaxVal(supportCount.get("A")[2]);
        intervalTrees.get("B").setMinVal(supportCount.get("B")[1]);
        intervalTrees.get("B").setMaxVal(supportCount.get("B")[2]);
        intervalTrees.get("C").setMinVal(supportCount.get("C")[1]);
        intervalTrees.get("C").setMaxVal(supportCount.get("C")[2]);
        intervalTrees.get("D").setMinVal(supportCount.get("D")[1]);
        intervalTrees.get("D").setMaxVal(supportCount.get("D")[2]);
    }

    public void testFullFPGrowth(){
        ColumnCount columnCount = new ColumnCount(MINSUP, new String[]{"A", "B", "C", "D", "E", "F", "G", "H"});

        Logger.getInstance().setTimer();
        parseQueries(new SupportCountParser(columnCount));
        Logger.getInstance().stopTimer("parseTime");

        setIntervalMinMax(columnCount);

        FullParser fullParser = new FullParser(columnCount);

        Logger.getInstance().setTimer();
        parseQueries(fullParser);
        Logger.getInstance().stopTimer("parseTime");

        FullFPTree fpTree = fullParser.getFpTree();

        Logger.getInstance().setTimer();
        fpTree.extractItemSets(MINSUP);
        Logger.getInstance().stopTimer("kernelRunTime");

        List<? extends IIndex> indexList = fpTree.getIndices();

        System.out.println("-- All generated Indexes --");
        int indexIDs = 0;
        for(IIndex idx : indexList) {
            indexIDs++;
            System.out.println(idx.createIdxStatementWithId(indexIDs));
        }

//        testIndexes(indexList, queryBatch);
    }

    public void testPartialFPGrowth(){
        ColumnCount columnCount = new ColumnCount(MINSUP, new String[]{"A", "B", "C", "D", "E", "F", "G", "H"});

        Logger.getInstance().setTimer();
        parseQueries(new SupportCountParser(columnCount));
        Logger.getInstance().stopTimer("parseTime");

        setIntervalMinMax(columnCount);

        InitializeFPTreeParser initialFPTreeParser = new InitializeFPTreeParser(columnCount);

        Logger.getInstance().setTimer();
        parseQueries(initialFPTreeParser);
        Logger.getInstance().stopTimer("parseTime");

        PopulateFPTreeParser fpTreeParser = initialFPTreeParser.buildFPTreeParser();

        Logger.getInstance().setTimer();
        parseQueries(fpTreeParser);
        Logger.getInstance().stopTimer("parseTime");

        ValidateFPTreeParser validator = fpTreeParser.buildValidateFPTreeParser();

        Logger.getInstance().setTimer();
        parseQueries(validator);
        Logger.getInstance().stopTimer("parseTime");

        PartialFPTree fpTree = validator.getFpTree();

        Logger.getInstance().setTimer();
        fpTree.extractItemSets(MINSUP);
        Logger.getInstance().stopTimer("kernelRunTime");

        List<? extends IIndex> indexList = fpTree.getIndices();

        System.out.println("-- All generated Indexes --");
        int indexIDs = 0;
        for(IIndex idx : indexList) {
            indexIDs++;
            System.out.println(idx.createIdxStatementWithId(indexIDs));
        }

//        testIndexes(indexList, queryBatch);
    }

    public void run(boolean enablePartialIdxs){
        Logger.getInstance().reset();

        List<IIndex> indexList = new ArrayList<>();

        ColumnCount columnCount = new ColumnCount(MINSUP, new String[]{"A", "B", "C", "D", "E", "F", "G", "H"});

        System.out.println("--- Mine Frequency ---");
        Logger.getInstance().setTimer();
        parseQueries(new SupportCountParser(columnCount));
        Logger.getInstance().stopTimer("parseTime");

        setIntervalMinMax(columnCount);

        System.out.println("--- Mine Predicates ---");
        Logger.getInstance().setTimer();
        parseQueries(new FullParser(columnCount));
        Logger.getInstance().stopTimer("parseTime");

        if(enablePartialIdxs)
            suggestPartialIndexes(indexList);
        else
            suggestFullIndexes(indexList);

        testIndexes(indexList, queryBatch);
    }


    private void parseQueries(IExpressionVisitor visitor){
//        ExpressionVisitor visitor = new FullParser(intervalTrees); // ToDo: pass visitor as argument instead to allow for polymorphism

        //CCJSqlParserManager parserManager = new CCJSqlParserManager();

        Select select;
        try {
//            Logger.getInstance().setTimer();
            Statements stats = CCJSqlParserUtil.parseStatements(queryBatch); // ToDo: Insertion into interval trees might take longer time than the actual parsing of queries (try to fix if possible)...
            for(Statement statement : stats.getStatements()){
                select = (Select) statement;
                PlainSelect ps = (PlainSelect) select.getSelectBody();

                Expression exp = ps.getWhere();

                visitor.before();
                exp.accept(visitor);
                visitor.after();

            }
//            Logger.getInstance().stopTimer("parseTime");
        } catch (JSQLParserException e1) {
            e1.printStackTrace();
        }
    }

    private void suggestFullIndexes(List<IIndex> indexList){
        for (Map.Entry<String, IntervalTree> entry : intervalTrees.entrySet())
        {
//            kernelPrepStartTime = System.nanoTime();
//            kernelPrepEstimatedTime += System.nanoTime() - kernelPrepStartTime;
//
//            kernelStartTime = System.nanoTime();
//            kernelEstimatedTime += System.nanoTime() - kernelStartTime;

            System.out.println("Column: " + entry.getKey() + "\tFrequency: " + entry.getValue().getFrequency());
            indexList.add(new FullIndex((double)entry.getValue().getFrequency(), 0, entry.getKey()));
        }
    }

    private void suggestPartialIndexes(List<IIndex> indexList){

        for (Map.Entry<String, IntervalTree> entry : intervalTrees.entrySet())
        {

            Logger.getInstance().setTimer();
            entry.getValue().iterate(); // ToDo: combine iterate and predictInterval
            Logger.getInstance().stopTimer("kernelLoadTime");

            Logger.getInstance().setTimer();
            double[][] interval = entry.getValue().predictIntervals(.9);
            Logger.getInstance().stopTimer("kernelRunTime");

            for (int p = 0; p < interval.length; p++) {
                System.out.println("Left: " + (interval[p][0]) + "\t Right: " + (interval[p][1]) + "\t Probability: " + (interval[p][2]));
                indexList.add(new PartialIndex(((double)entry.getValue().getFrequency() * (interval[p][2])), 0, entry.getKey(), (int)interval[p][0], (int)interval[p][1]));
            }
        }
    }

    // ToDo: Maybe pass a list of individual queries and not all queries in same string
    private void testIndexes(List<? extends IIndex> indexList, String queryBatch){
        PostgreSql postSql = null;
        try {
            postSql = new PostgreSql();
            postSql.estimateWeights(indexList);

            DynamicProgramming.solveKP(indexList, 52582912);

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
