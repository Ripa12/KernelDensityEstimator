package interval_tree;


import interval_tree.Factory.QueryGenerator;


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

        Experiment exp = new Experiment(q);

        exp.run(false);
        String fullIndexInfo = exp.toString();

        exp.run(true);
        String partialIndexInfo = exp.toString();

        System.out.println("generatorStartTime: " + generatorEstimatedTime/ 1000000000.0);

        System.out.println("-- Full Index --");
        System.out.println(fullIndexInfo);

        System.out.println("-- Partial Index --");
        System.out.println(partialIndexInfo);
    }
}
