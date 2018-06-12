package interval_tree;

import interval_tree.CandidateIndex.*;
import interval_tree.DBMS.PostgreSql;
import interval_tree.Factory.TablePropertiesBuilder;
import interval_tree.FrequentPatternMining.FullFPTree;
import interval_tree.FrequentPatternMining.PartialFPTree;
import interval_tree.FrequentPatternMining.SupportCount.TableProperties;
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
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static interval_tree.Globals.MINSUP;
import static interval_tree.Globals.STORAGE_CAPACITY;

public class Experiment {
    //https://github.com/lodborg/interval-tree/tree/master/src/main/java/com/lodborg/intervaltree

    // https://github.com/bnjmn/weka/blob/master/weka/src/main/java/weka/estimators/KernelEstimator.java
    // https://www.programcreek.com/java-api-examples/index.php?source_dir=Weka-for-Android-master/src/weka/classifiers/meta/RegressionByDiscretization.java#

    /**
     * Batch of queries
     */
    private String sourcePath;


    /**
     * Constructor
     */
    public Experiment(String filename){

        Logger.getInstance()
                .addTimer("SupportCountParser")
                .addTimer("ExtractItem-sets")
                .addTimer("queryGenerationTime")
                .addTimer("FullParser")
                .addTimer("InitializePartialFPTreeParser")
                .addTimer("ValidatePartialFPTreeParser")
                .addTimer("PopulatePartialFPTreeParser");

        this.sourcePath = "data/testdata/unittests/" + filename;
        if (SystemUtils.IS_OS_WINDOWS) {
            this.sourcePath = this.sourcePath.replaceFirst("/", "//");
        }
    }


    public void testFullFPGrowth(TableProperties tableCount){

        Logger.getInstance().setTimer();
        parseQueries(new SupportCountParser(tableCount));
        Logger.getInstance().stopTimer("SupportCountParser");


        Logger.getInstance().setTimer();
        FullParser fullParser = new FullParser(tableCount);
        parseQueries(fullParser);
        Logger.getInstance().stopTimer("FullParser");


        Logger.getInstance().setTimer();
        List<FullFPTree> fpTree = fullParser.getFpTree(); // ToDo: Should probably run more tests to see if multiple tables are handled properly.
        List<IIndex> indexList = new LinkedList<>();
        for (FullFPTree fullFPTree : fpTree) {
            fullFPTree.findFrequentPatterns(MINSUP);
            indexList.addAll(fullFPTree.getFullIndexes());
        }
        Logger.getInstance().stopTimer("ExtractItem-sets");

        System.out.println("-- All generated Indexes --");
        int indexIDs = 0;
        for(IIndex idx : indexList) {
            indexIDs++;
            System.out.println(idx.createIdxStatementWithId(indexIDs) + " val " + idx.getValue());
        }

//        testIndexes(indexList);
    }

    public void testPartialFPGrowth(TableProperties tableCount){

        Logger.getInstance().setTimer();
        parseQueries(new SupportCountParser(tableCount));
        Logger.getInstance().stopTimer("SupportCountParser");


        Logger.getInstance().setTimer();
        InitializeFPTreeParser initialFPTreeParser = new InitializeFPTreeParser(tableCount);
        parseQueries(initialFPTreeParser);
        Logger.getInstance().stopTimer("InitializePartialFPTreeParser");


        Logger.getInstance().setTimer();
        PopulateFPTreeParser fpTreeParser = initialFPTreeParser.buildFPTreeParser();
        parseQueries(fpTreeParser);
        Logger.getInstance().stopTimer("PopulatePartialFPTreeParser");


        Logger.getInstance().setTimer();
        ValidateFPTreeParser validator = fpTreeParser.buildValidateFPTreeParser();
        parseQueries(validator);
        Logger.getInstance().stopTimer("ValidatePartialFPTreeParser");

        Logger.getInstance().setTimer();
        List<PartialFPTree> fpTree = validator.getFpTree();
        List<IIndex> partialIndices = new LinkedList<>();
        List<IIndex> fullIndices = new LinkedList<>();

        for (PartialFPTree partialFPTree : fpTree) {
            partialFPTree.extractItemSets(MINSUP);
            partialFPTree.findFrequentPatterns(MINSUP);
            partialIndices.addAll(partialFPTree.getPartialIndexes());
            fullIndices.addAll(partialFPTree.getFullIndexes());
        }
        Logger.getInstance().stopTimer("ExtractItem-sets");

        System.out.println("-- All generated Partial Indexes --");
        int indexIDs = 0;
        for(IIndex idx : partialIndices) {
            indexIDs++;
            System.out.println(idx.createIdxStatementWithId(indexIDs) + " val " + idx.getValue());
        }

        System.out.println("-- All generated Full Indexes --");
        for(IIndex idx : fullIndices) {
            indexIDs++;
            System.out.println(idx.createIdxStatementWithId(indexIDs) + " val " + idx.getValue());
        }


//        testIndexes(partialIndices, fullIndices);
    }

    private void parseQueries(IExpressionVisitor visitor){

        Select select;
        try(BufferedReader br = new BufferedReader(new FileReader(sourcePath))){

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
    private void testIndexes(List<? extends IIndex> indexList){
        PostgreSql postSql = null;
        try {
            postSql = new PostgreSql();
            postSql.estimateWeights(indexList);

            DynamicProgramming.solveKP(indexList, STORAGE_CAPACITY);

            postSql.buildCandidateIndexes(indexList);
            postSql.testIndexes(sourcePath);

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
                             List<IIndex> fullIndices){
        PostgreSql postSql = null;
        try {
            postSql = new PostgreSql();
            postSql.estimateWeights(fullIndices);

            int leftover = DynamicProgramming.solveKP(fullIndices, STORAGE_CAPACITY);

            postSql.buildCandidateIndexes(fullIndices);

            // ToDo: Is the time for index pruning recorded?
            for (IIndex fullIndex : fullIndices) {
                for(int i = partialIndices.size()-1; i >= 0; i--){
                    if(fullIndex.isAPrefix(partialIndices.get(i).getColumnName())){
                        partialIndices.remove(i);
                    }
                }
            }

            postSql.estimateWeights(partialIndices);

            System.out.println(leftover);

            DynamicProgramming.solveKP(partialIndices, STORAGE_CAPACITY - leftover);

            postSql.buildCandidateIndexes(partialIndices);

            postSql.testIndexes(sourcePath);

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
