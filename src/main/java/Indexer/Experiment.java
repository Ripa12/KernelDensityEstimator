package Indexer;

import Indexer.CandidateIndex.*;
import Indexer.DBMS.PostgreSql;
import Indexer.Factory.SupportCountParser;
import Indexer.Factory.TableBaseProperties;
import Indexer.FrequentPatternMining.Full.FullFPTree;
import Indexer.FrequentPatternMining.Partial.PartialFPTree;
import Indexer.GUI.Menu;
import Indexer.KnapsackProblem.DynamicProgramming;
import Indexer.Logger.Logger;
import Indexer.SqlParser.*;
import Indexer.SqlParser.FullParser.FullParser;
import Indexer.SqlParser.PartialParser.PopulateFPTreeParser;
import Indexer.SqlParser.PartialParser.InitializeFPTreeParser;
import Indexer.SqlParser.PartialParser.ValidateFPTreeParser;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import static Indexer.Utility.getOutputDirectory;

public class Experiment {
    /**
     * Batch of queries
     */
    private String filename;
    private Menu menu;

    /**
     * Constructor
     */
    public Experiment(Menu menu, String filename){

        Logger.getInstance()
                .addTimer("SupportCountParser")
                .addTimer("ExtractItem-sets")
                .addTimer("queryGenerationTime")
                .addTimer("FullParser")
                .addTimer("InitializePartialFPTreeParser")
                .addTimer("ValidatePartialFPTreeParser")
                .addTimer("PopulatePartialFPTreeParser")
                .addTimer("FinalOptimization")
                .addTimer("QueryBatchTime");

        if (SystemUtils.IS_OS_WINDOWS) {
            filename = filename.replaceFirst("/", "//");
        }
        this.filename = filename;

        this.menu = menu;
    }


    public void testFullFPGrowth(TableBaseProperties tableCount){

        Logger.getInstance().setTimer("SupportCountParser");
        SupportCountParser scp = new SupportCountParser(tableCount, menu.getDoubleProperty("MINIMUM_SUPPORT"));
        parseQueries(scp);
        Logger.getInstance().stopTimer("SupportCountParser");


        Logger.getInstance().setTimer("FullParser");
        FullParser fullParser = new FullParser(menu, scp.getStats());
        parseQueries(fullParser);
        Logger.getInstance().stopTimer("FullParser");


        Logger.getInstance().setTimer("ExtractItem-sets");
        List<FullFPTree> fpTree = fullParser.getFpTree();
        List<IIndex> indexList = new LinkedList<>();
        for (FullFPTree fullFPTree : fpTree) {
            fullFPTree.findFrequentPatterns(menu.getDoubleProperty("MINIMUM_SUPPORT"));
            indexList.addAll(fullFPTree.getFullIndexes());
        }
        Logger.getInstance().stopTimer("ExtractItem-sets");

        System.out.println("\n-- Former Indexer --\n");
        System.out.println("-- All generated Indexes --");
        int indexIDs = 0;
        for(IIndex idx : indexList) {
            indexIDs++;
            System.out.println(idx.createIdxStatementWithId(indexIDs, tableCount) + " val " + idx.getValue());
        }

//        testIndexes(indexList, tableCount);
    }

    public void testPartialFPGrowth(TableBaseProperties tableCount){

        Logger.getInstance().setTimer("SupportCountParser");
        SupportCountParser scp = new SupportCountParser(tableCount, menu.getDoubleProperty("MINIMUM_SUPPORT"));
        parseQueries(scp);
        Logger.getInstance().stopTimer("SupportCountParser");


        Logger.getInstance().setTimer("InitializePartialFPTreeParser");
        InitializeFPTreeParser initialFPTreeParser = new InitializeFPTreeParser(menu, scp.getStats());
        parseQueries(initialFPTreeParser);
        Logger.getInstance().stopTimer("InitializePartialFPTreeParser");


        Logger.getInstance().setTimer("PopulatePartialFPTreeParser");
        PopulateFPTreeParser fpTreeParser = initialFPTreeParser.buildFPTreeParser();
        parseQueries(fpTreeParser);
        Logger.getInstance().stopTimer("PopulatePartialFPTreeParser");


        Logger.getInstance().setTimer("ValidatePartialFPTreeParser");
        ValidateFPTreeParser validator = fpTreeParser.buildValidateFPTreeParser();
        parseQueries(validator);
        Logger.getInstance().stopTimer("ValidatePartialFPTreeParser");

        Logger.getInstance().setTimer("ExtractItem-sets");
        List<PartialFPTree> fpTree = validator.getFpTree();
        List<IIndex> partialIndices = new LinkedList<>();
        List<IIndex> fullIndices = new LinkedList<>();

        for (PartialFPTree partialFPTree : fpTree) {
            partialFPTree.extractItemSets(menu.getDoubleProperty("MINIMUM_SUPPORT"));
            partialFPTree.findFrequentPatterns(menu.getDoubleProperty("MINIMUM_SUPPORT"));
            partialIndices.addAll(partialFPTree.getPartialIndexes());
            fullIndices.addAll(partialFPTree.getFullIndexes());
        }
        Logger.getInstance().stopTimer("ExtractItem-sets");

        System.out.println("\n-- Extended Indexer --\n");
        System.out.println("-- All generated Partial Indexes --");
        int indexIDs = 0;
        for(IIndex idx : partialIndices) {
            indexIDs++;
            System.out.println(idx.createIdxStatementWithId(indexIDs, tableCount) + " val " + idx.getValue());
        }

        System.out.println("-- All generated Full Indexes --");
        for(IIndex idx : fullIndices) {
            indexIDs++;
            System.out.println(idx.createIdxStatementWithId(indexIDs, tableCount) + " val " + idx.getValue());
        }


//        testIndexes(partialIndices, fullIndices, tableCount);
    }

    private void parseQueries(IExpressionVisitor visitor){
        Select select;

        try(BufferedReader br = new BufferedReader(new FileReader(new File(getOutputDirectory() + this.filename)))){

            for(String line; (line = br.readLine()) != null; ) {
                Statement stat = CCJSqlParserUtil.parse(line); // ToDo: Insertion into interval trees might take longer time than the actual parsing of queries (try to fix if possible)...

                select = (Select) stat;
                PlainSelect ps = (PlainSelect) select.getSelectBody();

                Expression exp = ps.getWhere();

                visitor.setCurrentTable(ps.getFromItem().toString());
                visitor.before();
                exp.accept(visitor);
                visitor.after();
            }

        } catch (JSQLParserException | IOException e1) {
            e1.printStackTrace();
        }
    }


    // ToDo: Maybe pass a list of individual queries and not all queries in same string
    private void testIndexes(List<? extends IIndex> indexList, TableBaseProperties tp){
        PostgreSql postSql = null;
        try {
            postSql = new PostgreSql();
            postSql.dropAllIndexes(tp);

            Logger.getInstance().setTimer("FinalOptimization");

            //
//            postSql.checkUtility(indexList, sourcePath, MIN_SUP, tp);
            //

            postSql.estimateWeights(indexList, tp);

            DynamicProgramming.solveKP(indexList, menu.getIntegerProperty("STORAGE_CAPACITY"));


            Logger.getInstance().stopTimer("FinalOptimization");

            postSql.buildCandidateIndexes(indexList, tp);

            Logger.getInstance().setNrOfIndexes(indexList.size());

            postSql.testIndexes(filename);


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

    private void testIndexes(List<IIndex> partialIndices,
                             List<IIndex> fullIndices, TableBaseProperties tp){
//        fullIndices.addAll(partialIndices); // ToDo: Check if partial- or full indexes are used by the planner, at the same time

        PostgreSql postSql = null;
        try {
            postSql = new PostgreSql();
            postSql.dropAllIndexes(tp);


            Logger.getInstance().setTimer("FinalOptimization");


            postSql.estimateWeights(fullIndices, tp);

            int leftover = DynamicProgramming.solveKP(fullIndices, menu.getIntegerProperty("STORAGE_CAPACITY"));

            //
//            postSql.checkUtility(fullIndices, sourcePath, MIN_SUP, tp);
            //

            Logger.getInstance().stopTimer("FinalOptimization");

            postSql.buildCandidateIndexes(fullIndices, tp);

            Logger.getInstance().setTimer("FinalOptimization");

            for (IIndex fullIndex : fullIndices) {
                for(int i = partialIndices.size()-1; i >= 0; i--){
                    if(fullIndex.containsPrefix(partialIndices.get(i))){
                        partialIndices.remove(i);
                    }
                }
            }

//            //
//            postSql.checkUtility(partialIndices, sourcePath, MIN_SUP, tp);
//            //

            postSql.estimateWeights(partialIndices, tp);

            System.out.println(leftover);

            DynamicProgramming.solveKP(partialIndices, menu.getIntegerProperty("STORAGE_CAPACITY") - leftover);


            Logger.getInstance().stopTimer("FinalOptimization");

            postSql.buildCandidateIndexes(partialIndices, tp);

            Logger.getInstance().setNrOfIndexes(partialIndices.size() + fullIndices.size());

            postSql.testIndexes(filename);

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
