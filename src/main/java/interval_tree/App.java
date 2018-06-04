package interval_tree;


import interval_tree.Factory.QueryGenerator;
import sun.rmi.runtime.Log;


/**
 * Hello world!
 *
 */
public class App
{
//[o,k,p, n,o,j,p, o,m,n,p, n,g,m, o,m,l,p, g,o,d,m, o,n,m,j,p, o,m,j,p, n,k,m, n,k,p, n,k,o, o,g,m, d,g, o,k,m, g,d,l, g,d,m, g,d,o, d,m, m,k,p, d,l, d,o, n,m,j,p, l,k,n, k,o,g,m, l,m, j,o, j,n, l,g,o, n,m, j,p, l,p, n,p, o,j,p, n,o,k,p, o,l,m, o,l,p, o,n,m, o,n,p, n,o,k,m, n,j,o, o,n,m,k,p, o,m,k,p, l,o,d,m, k,g,n, n,j,p, l,g,o,d,m, o,d,m, n,o,g,m, k,n,g,m, g,l,d,o, g,l,d,m, k,g,m, l,d,o, b, l,d,m, m,l,p, d, m,n,p, f, g,l, g, g,k, g,n, g,m, n,m,k,p, j, k,l, k, g,o, l, k,n, m, k,m, k,p, n, o, k,o, p, m,p, n,k,o,g,m, o,m, m,j,p]
//[o,k,p, n,o,j,p, o,m,n,p, n,g,m, o,m,l,p, g,o,d,m, o,n,m,j,p, o,m,j,p, n,k,m, n,k,p, n,k,o, o,g,m, d,g, o,k,m, g,d,l, g,d,m, g,d,o, d,m, m,k,p, d,l, d,o, n,m,j,p, l,k,n, k,o,g,m, l,m, j,o, j,n, l,g,o, n,m, j,p, l,p, n,p, o,j,p, n,o,k,p, o,l,m, o,l,p, o,n,m, o,n,p, n,o,k,m, n,j,o, o,n,m,k,p, o,m,k,p, l,o,d,m, k,g,n, n,j,p, l,g,o,d,m, o,d,m, n,o,g,m, k,n,g,m, g,l,d,o, g,l,d,m, k,g,m, l,d,o, b, l,d,m, m,l,p, d, m,n,p, f, g,l, g, g,k, g,n, g,m, n,m,k,p, j, k,l, k, g,o, l, k,n, m, k,m, k,p, n, o, k,o, p, m,p, n,k,o,g,m, o,m, m,j,p]

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
