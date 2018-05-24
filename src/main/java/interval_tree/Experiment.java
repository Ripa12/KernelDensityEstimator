package interval_tree;

import interval_tree.CandidateIndex.*;
import interval_tree.DBMS.PostgreSql;
import interval_tree.DataStructure.IntervalTree;
import interval_tree.FrequentPatternMining.FullFPTree;
import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.FrequentPatternMining.SupportCount.TableCount;
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
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS;

import java.sql.SQLException;
import java.util.*;

public class Experiment {
    //https://github.com/lodborg/interval-tree/tree/master/src/main/java/com/lodborg/intervaltree

    // https://github.com/bnjmn/weka/blob/master/weka/src/main/java/weka/estimators/KernelEstimator.java
    // https://www.programcreek.com/java-api-examples/index.php?source_dir=Weka-for-Android-master/src/weka/classifiers/meta/RegressionByDiscretization.java#


    public static double MINSUP = .05;

    /**
     * Batch of queries
     */
    private String queryBatch;


    /**
     * Constructor
     */
    public Experiment(String batch){

        Logger.getInstance()
                .addTimer("kernelLoadTime")
                .addTimer("kernelRunTime")
                .addTimer("queryGenerationTime").addTimer("parseTime");


        queryBatch = batch;
    }

    private TableCount initiateTables(){
        TableCount tableCount = new TableCount(MINSUP, new String[]{"TestTable"});
        tableCount.addColumns("TestTable", new String[]{"A", "B", "C", "D", "E", "F", "G", "H"});
        return tableCount;
    }


    public void testFullFPGrowth(){
        TableCount tableCount = initiateTables();

        Logger.getInstance().setTimer();
        parseQueries(new SupportCountParser(tableCount));
        Logger.getInstance().stopTimer("parseTime");


        Logger.getInstance().setTimer();
        FullParser fullParser = new FullParser(tableCount);
        parseQueries(fullParser);
        Logger.getInstance().stopTimer("parseTime");


        Logger.getInstance().setTimer();
        List<FullFPTree> fpTree = fullParser.getFpTree(); // ToDo: Should probably run more tests to see if multiple tables are handled properly.
        List<IIndex> indexList = new LinkedList<>();
        for (FullFPTree fullFPTree : fpTree) {
            fullFPTree.extractItemSets(MINSUP);
            indexList.addAll(fullFPTree.getIndices());
        }
        Logger.getInstance().stopTimer("kernelRunTime");

        System.out.println("-- All generated Indexes --");
        int indexIDs = 0;
        for(IIndex idx : indexList) {
            indexIDs++;
            System.out.println(idx.createIdxStatementWithId(indexIDs));
        }

//        testIndexes(indexList, queryBatch);
    }

    public void testPartialFPGrowth(){
        TableCount tableCount = initiateTables();

        Logger.getInstance().setTimer();
        parseQueries(new SupportCountParser(tableCount));
        Logger.getInstance().stopTimer("parseTime");


        Logger.getInstance().setTimer();
        InitializeFPTreeParser initialFPTreeParser = new InitializeFPTreeParser(tableCount);
        parseQueries(initialFPTreeParser);
        Logger.getInstance().stopTimer("parseTime");


        Logger.getInstance().setTimer();
        PopulateFPTreeParser fpTreeParser = initialFPTreeParser.buildFPTreeParser();
        parseQueries(fpTreeParser);
        Logger.getInstance().stopTimer("parseTime");


        Logger.getInstance().setTimer();
        ValidateFPTreeParser validator = fpTreeParser.buildValidateFPTreeParser();
        parseQueries(validator);
        Logger.getInstance().stopTimer("parseTime");

        Logger.getInstance().setTimer();
        List<PartialFPTree> fpTree = validator.getFpTree();
        List<IIndex> indexList = new LinkedList<>();

        for (PartialFPTree partialFPTree : fpTree) {
            partialFPTree.extractItemSets(MINSUP);
            indexList.addAll(partialFPTree.getIndices());
        }
        Logger.getInstance().stopTimer("kernelRunTime");


        System.out.println("-- All generated Indexes --");
        int indexIDs = 0;
        for(IIndex idx : indexList) {
            indexIDs++;
            System.out.println(idx.createIdxStatementWithId(indexIDs));
        }

//        testIndexes(indexList, queryBatch);
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

                visitor.setCurrentTable(ps.getFromItem().toString());
                visitor.before();
                exp.accept(visitor);
                visitor.after();

            }
//            Logger.getInstance().stopTimer("parseTime");
        } catch (JSQLParserException e1) {
            e1.printStackTrace();
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
