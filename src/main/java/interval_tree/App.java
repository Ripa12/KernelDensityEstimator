package interval_tree;


import interval_tree.Factory.QueryGenerator;
import sun.rmi.runtime.Log;


/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        long generatorStartTime = System.nanoTime();
        QueryGenerator.generateBatchOfQueries("query_batch.txt");
        long generatorEstimatedTime = System.nanoTime() - generatorStartTime;

//        Experiment exp = new Experiment(QueryGenerator.csvToSql("test_data.csv"));
        Experiment exp = new Experiment("query_batch.txt");


        System.out.println("-- Partial Compound Index --");
        exp.testPartialFPGrowth();
        String partialIndexInfo = Logger.getInstance().toString();

        Logger.getInstance().reset();
        System.out.println("-- Full Index --");
        exp.testFullFPGrowth();
        String fullIndexInfo = Logger.getInstance().toString();



        System.out.println("generatorStartTime: " + generatorEstimatedTime/ 1000000000.0);

        System.out.println("-- Full Index --");
        System.out.println(fullIndexInfo);

        System.out.println("-- Partial Index --");
        System.out.println(partialIndexInfo);
    }
}
