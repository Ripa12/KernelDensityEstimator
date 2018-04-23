package interval_tree.Factory;


import interval_tree.DataStructure.IntervalTree;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by Richard on 2018-03-27.
 */
public class QueryGenerator {

    private final static String COLUMNS[] = {"B", "C", "D"};
    private final static int NR_OF_QUERIES = 2000;
    private final static int MAX_DUPLICATES = 40;
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
//        if (rand.nextInt(3) > 0) {
        if (false) {
            int start;
            int end;

            int random = rand.nextInt(101);
            if (random <= 67) { //This is 20% more
                start = rand.nextInt((FIRST_UPPER_BOUND - FIRST_LOWER_BOUND) + 1) + FIRST_LOWER_BOUND;
                end = rand.nextInt((FIRST_UPPER_BOUND - start) + 1) + start;
            } else if (random <= 89) {
                start = rand.nextInt((SECOND_UPPER_BOUND - SECOND_LOWER_BOUND) + 1) + SECOND_LOWER_BOUND;
                end = rand.nextInt((SECOND_UPPER_BOUND - start) + 1) + start;
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

    static void generateCSV(String filename){
        String path = String.valueOf(ClassLoader.getSystemClassLoader().getResource(filename).getPath());
        if (SystemUtils.IS_OS_WINDOWS) {
            path = path.replaceFirst("/", "");
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(
                    new BufferedOutputStream(new FileOutputStream(path)), "UTF-8"));

            Random rand = new Random();

            for(int i = 0; i < NR_OF_QUERIES; i++) {

                    int random;
                    int[] row = new int[3];

                    for (int k = 0; k < 3; k++) {
                        random = rand.nextInt(101);
                        if (random <= 70) { //This is 20% more
                            row[k] = rand.nextInt((FIRST_UPPER_BOUND - FIRST_LOWER_BOUND) + 1) + FIRST_LOWER_BOUND;
                        } else if (random <= 90) {
                            row[k] = rand.nextInt((SECOND_UPPER_BOUND - SECOND_LOWER_BOUND) + 1) + SECOND_LOWER_BOUND;
                        } else {
                            row[k] = rand.nextInt(MAX_UPPER_BOUND);
                        }
                    }
                    int total = 1;
                    if (rand.nextInt(10) > 0)
                        total = rand.nextInt(MAX_DUPLICATES)+1;
                    for (int t = 0; t < total; t++) {
                        out.print(String.format("%d %d %d%n", row[0], row[1], row[2]));
                    }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(out != null) {
                out.flush();
                out.close();
            }
        }
    }

    public static String csvToSql(String source){

        /*
            source file
         */
//        String sourcePath = String.valueOf(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(source)).getPath());
        String sourcePath = "data/testdata/unittests/" + source;
        if (SystemUtils.IS_OS_WINDOWS) {
            sourcePath = sourcePath.replaceFirst("/", "");
        }

        StringBuilder statements = new StringBuilder();
        StringBuilder statement = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(sourcePath))) {


            for(String line; (line = br.readLine()) != null; ) {

                String[] columns = line.split(" ");

                statement.setLength(0);
                statement.append("SELECT * FROM TestTable WHERE ");

                statement.append(" B = ").append(columns[0]).append(" AND ");
                statement.append("C = ").append(columns[1]).append(" AND ");
                statement.append("D = ").append(columns[2]).append(";");
                statements.append(statement.toString()).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return statements.toString();
    }

}
