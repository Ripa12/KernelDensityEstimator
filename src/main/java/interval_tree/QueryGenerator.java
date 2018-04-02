package interval_tree;


import java.util.Random;

/**
 * Created by Richard on 2018-03-27.
 */
public class QueryGenerator {

    final static String COLUMNS[] = {"A", "B", "C"};
//    final static long RANGE[] = {0, 100000000L};
    final static int NR_OF_QUERIES = 5000;

    private static int nrOfQueries = 0;

    static String generateBatchOfQueries(){
        nrOfQueries = 0;

        String sqlPrefix = "SELECT * FROM Customers WHERE ";
        StringBuilder stmts = new StringBuilder();

        Random rand = new Random();

        final int maxDuplicates = 300;
        final int maxUpperBound = 300000;

        final int firstLowerBound = 20000;
        final int firstUpperBound = 75000;

        final int secondLowerBound = 100000;
        final int secondUpperBound = 120000;

        StringBuilder tempStmt = new StringBuilder();
        for(int k = 0; k < NR_OF_QUERIES; k++) {
            int selectedColumn = rand.nextInt(COLUMNS.length);
            tempStmt.setLength(0);
            tempStmt.append(sqlPrefix);
            if (rand.nextInt(3) > 0) {
                int start;
                int end;

                int random = rand.nextInt(101);
                if (random <= 87) { //This is 20% more
                    start = rand.nextInt((firstUpperBound - firstLowerBound) + 1) + firstLowerBound;
                    end = rand.nextInt((firstUpperBound - start) + 1) + start;
                }
                else if(random <= 92){
                    start = rand.nextInt((secondUpperBound - secondLowerBound) + 1) + secondLowerBound;
                    end = rand.nextInt((secondUpperBound - start) + 1) + start;
                }
                else {
                    start = rand.nextInt(maxUpperBound);
                    end = rand.nextInt((maxUpperBound - start) + 1) + start;
                }

                tempStmt.append(COLUMNS[selectedColumn])
                        .append(" < ").append(start).append(" AND ")
                        .append(end).append(" < ").append(COLUMNS[selectedColumn])
                        .append(";\n");
            }
            else {
                int start;

                int random = rand.nextInt(101);
                if (random <= 87) { //This is 20% more
                    start = rand.nextInt((firstUpperBound - firstLowerBound) + 1) + firstLowerBound;
                }
                else if(random <= 92){
                    start = rand.nextInt((secondUpperBound - secondLowerBound) + 1) + secondLowerBound;
                }
                else {
                    start = rand.nextInt(maxUpperBound);
                }
                tempStmt.append(COLUMNS[selectedColumn]).append(" = ")
                        .append(start).append(";\n");
            }

            int total = 1;
            if (rand.nextInt(4) > 0)
                total = rand.nextInt(maxDuplicates)+1;
            for (int t = 0; t < total; t++) {
                stmts.append(tempStmt.toString());
                nrOfQueries++;
            }
        }

        System.out.println("NrOfQueries: " + nrOfQueries);

        return stmts.toString();
    }
}
