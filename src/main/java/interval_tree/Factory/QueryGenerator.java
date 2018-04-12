package interval_tree.Factory;


import java.util.Random;

/**
 * Created by Richard on 2018-03-27.
 */
public class QueryGenerator {

    private final static String COLUMNS[] = {"A", "B", "C", "D"};
    private final static int NR_OF_QUERIES = 400;
    private final static int MAX_DUPLICATES = 50;
    private final static int MAX_UPPER_BOUND = 450000;
    private final static int FIRST_LOWER_BOUND = 0;
    private final static int FIRST_UPPER_BOUND = 45000;
    private final static int SECOND_LOWER_BOUND = 420000;
    private final static int SECOND_UPPER_BOUND = 450000;

    private static Random rand;

    public static String generateBatchOfQueries(){
        int nrOfQueries = 0;
        int nrOfPredicates = 0;
        int localNrOfPredicates;

        String sqlPrefix = "SELECT * FROM TestTable WHERE ";
        StringBuilder stmts = new StringBuilder();

        //Random rand = new Random();
        rand = new Random();



        StringBuilder tempStmt = new StringBuilder();
        for(int k = 0; k < NR_OF_QUERIES; k++) {

            tempStmt.setLength(0);
            tempStmt.append(sqlPrefix);

            int selectedColumn = 0;
            selectedColumn = rand.nextInt((COLUMNS.length - selectedColumn)) + selectedColumn;
            generatePredicate(tempStmt, selectedColumn);
            localNrOfPredicates = 1;


            while(rand.nextInt(2) > 0 && selectedColumn < (COLUMNS.length - 1)) {
                selectedColumn++;
                selectedColumn = rand.nextInt((COLUMNS.length - selectedColumn)) + selectedColumn;

                tempStmt.append(" AND ");

                generatePredicate(tempStmt, selectedColumn);
                localNrOfPredicates++;
            }

            tempStmt.append(";\n");

            int total = 1;
            if (rand.nextInt(4) > 0)
                total = rand.nextInt(MAX_DUPLICATES)+1;
            for (int t = 0; t < total; t++) {
                stmts.append(tempStmt.toString());
                nrOfQueries++;
                nrOfPredicates += localNrOfPredicates;
            }

        }

        // ToDo: Remember A is a clustered key!
//        stmts.append(sqlPrefix).append("A = 750000000;\n"); //ToDo: Testing purposes

        System.out.println("NrOfQueries: " + nrOfQueries);
        System.out.println("NrOfPredicates: " + nrOfPredicates);

        return stmts.toString();
    }

    public static void generatePredicate(StringBuilder tempStmt, int selectedColumn){
        if (rand.nextInt(3) > 0) {
            int start;
            int end;

            int random = rand.nextInt(101);
            if (random <= 67) { //This is 20% more
                start = rand.nextInt((FIRST_UPPER_BOUND - FIRST_LOWER_BOUND) + 1) + FIRST_LOWER_BOUND;
                end = rand.nextInt((FIRST_UPPER_BOUND - start) + 1) + start;
            } else if (random <= 89) {
//                start = rand.nextInt((SECOND_UPPER_BOUND - SECOND_LOWER_BOUND) + 1) + SECOND_LOWER_BOUND;
//                end = rand.nextInt((SECOND_UPPER_BOUND - start) + 1) + start;
                start = SECOND_LOWER_BOUND;
                end = SECOND_UPPER_BOUND;
            } else {
                start = rand.nextInt(MAX_UPPER_BOUND);
                end = rand.nextInt((MAX_UPPER_BOUND - start) + 1) + start;
            }

            tempStmt.append(COLUMNS[selectedColumn])
                    .append(" < ")
                    .append(start)
                    .append(" AND ")
                    .append(end)
                    .append(" < ")
                    .append(COLUMNS[selectedColumn]);

        } else {
            int start;

            int random = rand.nextInt(101);
            if (random <= 87) { //This is 20% more
                start = rand.nextInt((FIRST_UPPER_BOUND - FIRST_LOWER_BOUND) + 1) + FIRST_LOWER_BOUND;
            } else if (random <= 92) {
                start = rand.nextInt((SECOND_UPPER_BOUND - SECOND_LOWER_BOUND) + 1) + SECOND_LOWER_BOUND;
            } else {
                start = rand.nextInt(MAX_UPPER_BOUND);
            }
            tempStmt.append(COLUMNS[selectedColumn])
                    .append(" = ")
                    .append(start);
        }
    }
}
