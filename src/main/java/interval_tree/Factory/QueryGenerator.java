package interval_tree.Factory;


import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Created by Richard on 2018-03-27.
 */
public class QueryGenerator {

//    public final static String TABLE_NAME = "UCI_CBM";
public final static String TABLE_NAME = "TestTable";

//    public final static String COLUMN_LABELS[] = {"GTT", "GTn", "GGn", "Ts", "Tp", "T48", "T2", "P2", "TIC", "mf"};
public final static String COLUMN_LABELS[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p"};
//    public final static double COLUMN_MIN_MAX[][] = {
//            {253.547, 72784.872}, // GTT
//            {1307.675, 3560.741}, // GTn
//            {6589.002, 9797.103}, // GGn
//            {5.304, 645.249}, // Ts
//            {5.304, 645.249}, // Tp
//            {442.364, 1115.797}, // T48
//            {540.442, 789.094}, // T2
////            {5.828, 23.14}, // P2
////            {0.0, 92.556}, // TIC
////            {0.068, 1.832}, // mf
//    };
//    public final static double COLUMN_DENSE_INTERVALS[][][] = {
//            {{300, 1000, 60}, {50000, 70000, 90}}, // GTT
//            {{1500, 1700, 60}, {2500, 2550, 90}},// GTn
//            {{7000, 7200, 60}, {8000, 8200, 90}},// GGn
//            {{10, 30, 60}, {450, 500, 90}},// Ts
//            {{10, 30, 60}, {450, 500, 90}},// Tp
//            {{500, 550, 60}, {700, 720, 90}},// T48
//            {{600, 620, 60}, {660, 690, 90}},// T2
////            {{6, 14, 60}, {18, 20, 90}},// P2
////            {{0, 20, 60}, {80, 84, 90}},// TIC
////            {{0.080, 0.130, 60}, {1.0, 1.3, 90}}// mf
//    };

    public final static double COLUMN_MIN_MAX[][] = {
            {-0, 100000}, // GTT 1
            {-0, 100000}, // GTn 2
            {-0, 100000}, // GGn 3
            {-0, 100000}, // Ts 4
            {-0, 100000}, // Tp 5
            {-0, 100000}, // T48 6
            {-0, 100000}, // T2 7
            {-0, 100000}, // P2 8
            {-0, 100000}, // TIC 9
            {-0, 100000}, // mf 10
            {-0, 100000}, // TIC 11
            {-0, 100000}, // mf 12
            {-0, 100000}, // TIC 13
            {-0, 100000}, // mf 14
            {-0, 100000}, // TIC 15
            {-0, 100000}, // mf 16
    };
    public final static double COLUMN_DENSE_INTERVALS[][][] = {
            {{300, 1000, 60}, {50000, 70000, 90}}, // GTT 1 a
            {{1500, 1700, 60}, {2500, 2550, 90}},// GTn 2 b
            {{7000, 7200, 60}, {8000, 8200, 90}},// GGn 3 c
            {{10, 30, 60}, {450, 500, 90}},// Ts 4 d
            {{10, 30, 60}, {450, 500, 90}},// Tp 5 e
            {{500, 550, 60}, {700, 720, 90}},// T48 6 f
            {{600, 620, 60}, {660, 690, 90}},// T2 7 g
            {{6, 14, 60}, {18, 20, 90}},// P2 8 h
            {{0, 20, 60}, {80, 84, 90}},// TIC 9 i
            {{300, 1000, 60}, {40000, 55000, 90}},// mf 10 j
            {{300, 8000, 60}, {55000, 70000, 90}},// mf 11 k
            {{300, 1800, 60}, {50000, 70000, 90}},// mf 12 l
            {{700, 1000, 60}, {55000, 67000, 90}},// mf 13 m
            {{3000, 10000, 60}, {50000, 70000, 90}},// mf 14 n
            {{300, 1000, 60}, {40000, 64700, 90}},// mf 15 o
            {{5800, 8000, 60}, {25000, 36000, 90}},// mf 16 p

    };


    private final static int NR_OF_QUERIES = 20; // ToDo: Null-pointer exception if very small
    private final static int MAX_DUPLICATES = 500;

    private static Random rand;

    public static final int COMPOSITE_PROBABILITY = 5;
    public static void generateBatchOfQueries(String filename){
        int nrOfQueries = 0;
        int nrOfPredicates = 0;
        int localNrOfPredicates;

        String targetPath = "data/testdata/unittests/" + filename;
        if (SystemUtils.IS_OS_WINDOWS) {
            targetPath = targetPath.replaceFirst("/", "//");
        }

        String sqlPrefix = "SELECT * FROM "+TABLE_NAME+" WHERE ";

        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(
                    new BufferedOutputStream(new FileOutputStream(targetPath)), "UTF-8"));

            rand = new Random();

            StringBuilder tempStmt = new StringBuilder();
            for (int k = 0; k < NR_OF_QUERIES; k++) {

                tempStmt.setLength(0);
                tempStmt.append(sqlPrefix);

                int selectedColumn = 0;
                selectedColumn = rand.nextInt((COLUMN_LABELS.length - selectedColumn)) + selectedColumn;
                generatePredicate(tempStmt, selectedColumn);
                localNrOfPredicates = 1;


                while (rand.nextInt(COMPOSITE_PROBABILITY) > 0 && selectedColumn < (COLUMN_LABELS.length - 1)) {
                    selectedColumn++;
                    selectedColumn = rand.nextInt((COLUMN_LABELS.length - selectedColumn)) + selectedColumn;

                    tempStmt.append(" AND ");

                    generatePredicate(tempStmt, selectedColumn);
                    localNrOfPredicates++;
                }

                tempStmt.append(";\n");

                int total = rand.nextInt(MAX_DUPLICATES) + 1;
                for (int t = 0; t < total; t++) {
                    out.print(tempStmt.toString());
                    nrOfQueries++;
                    nrOfPredicates += localNrOfPredicates;
                }

            }
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        } finally {
        if(out != null) {
            out.flush();
            out.close();
        }
    }

        System.out.println("NrOfQueries: " + nrOfQueries);
        System.out.println("NrOfPredicates: " + nrOfPredicates);
    }

    private static final int INTERVAL_PROBABILITY = 3;
    public static void generatePredicate(StringBuilder tempStmt, int selectedColumn){

        if (rand.nextInt(INTERVAL_PROBABILITY) > 0) {
            int start = 0;
            int end = 0;

            int random = ThreadLocalRandom.current().nextInt(100);
            boolean success = false;

            for (double[] denseInterval : COLUMN_DENSE_INTERVALS[selectedColumn]) {
                if (!success && random <= denseInterval[2]){
                    success = true;
//                    start = ThreadLocalRandom.current().nextDouble(denseInterval[1] + 1.0 - denseInterval[0]) + denseInterval[0];
//                    end = ThreadLocalRandom.current().nextDouble(denseInterval[1] + 1.0 - start) + start;
                    start = ThreadLocalRandom.current().nextInt((int)(denseInterval[1] + 1.0 - denseInterval[0])) + (int)denseInterval[0];
                    end = ThreadLocalRandom.current().nextInt((int)(denseInterval[1] + 1.0 - start)) + start;
                }
            }

            if(!success){
//                start = ThreadLocalRandom.current().nextDouble((COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - COLUMN_MIN_MAX[selectedColumn][0])) + COLUMN_MIN_MAX[selectedColumn][0];
//                end = ThreadLocalRandom.current().nextDouble((COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - start)) + start;
                start = ThreadLocalRandom.current().nextInt((int)(COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - COLUMN_MIN_MAX[selectedColumn][0])) + (int)COLUMN_MIN_MAX[selectedColumn][0];
                end = ThreadLocalRandom.current().nextInt((int)(COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - start)) + start;
            }

            tempStmt.append(COLUMN_LABELS[selectedColumn])
                    .append(" < ")
                    .append(start)
                    .append(" AND ")
                    .append(end)
                    .append(" < ")
                    .append(COLUMN_LABELS[selectedColumn]);

        } else {
            int start = 0;

            int random = ThreadLocalRandom.current().nextInt(100);
            boolean success = false;

            for (double[] denseInterval : COLUMN_DENSE_INTERVALS[selectedColumn]) {
                if (!success && random <= denseInterval[2]){
                    success = true;
//                    start = ThreadLocalRandom.current().nextDouble((denseInterval[1] + 1.0 - denseInterval[0])) + denseInterval[0];
                    start = ThreadLocalRandom.current().nextInt((int)(denseInterval[1] + 1.0 - denseInterval[0])) + (int)denseInterval[0];
                }
            }
            if(!success) {
//                start = ThreadLocalRandom.current().nextDouble((COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - COLUMN_MIN_MAX[selectedColumn][0])) + COLUMN_MIN_MAX[selectedColumn][0];
                start = ThreadLocalRandom.current().nextInt((int)(COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - COLUMN_MIN_MAX[selectedColumn][0])) + (int)COLUMN_MIN_MAX[selectedColumn][0];
            }

            tempStmt.append(COLUMN_LABELS[selectedColumn])
                    .append(" = ")
                    .append(start);
        }
    }
}
