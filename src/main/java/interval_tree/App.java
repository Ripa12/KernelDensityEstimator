package interval_tree;


import interval_tree.Factory.QueryGenerator;
import interval_tree.SubspaceClustering.Clique;


/**
 * Hello world!
 *
 */
public class App
{

    public static void main( String[] args )
    {
        long generatorStartTime = System.nanoTime();
        String q = QueryGenerator.generateBatchOfQueries();
        long generatorEstimatedTime = System.nanoTime() - generatorStartTime;

//        Experiment exp = new Experiment(QueryGenerator.csvToSql("test_data.csv"));
        Experiment exp = new Experiment(q);

        System.out.println("-- Partial Compound Index --");
//        exp.testFPGrowth();

        exp.testFPGrowth();

//        exp.run(true);
        String partialIndexInfo = Logger.getInstance().toString();

//        exp.run(false);
        String fullIndexInfo = Logger.getInstance().toString();


        System.out.println("generatorStartTime: " + generatorEstimatedTime/ 1000000000.0);

        System.out.println("-- Full Index --");
        System.out.println(fullIndexInfo);

        System.out.println("-- Partial Index --");
        System.out.println(partialIndexInfo);
    }
}
