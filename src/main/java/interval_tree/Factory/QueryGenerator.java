package interval_tree.Factory;


import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.*;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Created by Richard on 2018-03-27.
 */
public class QueryGenerator {

//    public final static String TABLE_NAME = "UCI_CBM";
//public final static String TABLE_NAME = "TestTable";

//    public final static String COLUMN_LABELS[] = {"GTT", "GTn", "GGn", "Ts", "Tp", "T48", "T2", "P2", "TIC", "mf"};
//public final static String COLUMN_LABELS[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p"};
    /**
     * Min Max IsDecimal(0 or 1)
     **/
//    public final static double COLUMN_MIN_MAX[][] = {
//            { 1, 39, 0 },
//        { 1, 46, 0 },
//        { 13, 1, 0 },
//        { 0, 1, 1 },
//        { 2, 23420, 0 },
//        { 1, 15.9729, 1 },
//        { 0.666667, 3.44, 1 },
//        { 0.00821018, 1, 1 },
//        { 0, 1.7962, 1 },
//        { 0, 3, 0 },
//        { 0, 4, 0 },
//        { 0, 220, 0 },
//        { 0.666667, 15.6048, 1 },
//        { 0.666667, 10.2222, 1 },
//        { 0, 2112.06, 1 },
//        { 0, 0.666667, 1 },
//        { 0, 3, 1 },
//        { 1, 10.2222, 1 },
//        { 0, 1, 1 },
//        { 0.333333, 1, 1 },
//        { 0.543728, 307446000, 1 }, //30744600000
//        { 0.666667, 25.0341, 1 },
//        { 0, 0.666667, 1 },
//        { 0.102206, 1, 1 },
//        { 0.666667, 12.667, 1 },
//        { 0, 1, 1 },
//        { 2, 232, 0 },
//        { 1, 444, 0 }
//    };
//    public final static double COLUMN_MIN_MAX[][] = {
//            {-0, 100000, 0}, // GTT 1
//            {-0, 100000, 0}, // GTn 2
//            {-0, 100000, 0}, // GGn 3
//            {-0, 100000, 0}, // Ts 4
//            {-0, 100000, 0}, // Tp 5
//            {-0, 100000, 0}, // T48 6
//            {-0, 100000, 0}, // T2 7
//            {-0, 100000, 0}, // P2 8
//            {-0, 100000, 0}, // TIC 9
//            {-0, 100000, 0}, // mf 10
//            {-0, 100000, 0}, // TIC 11
//            {-0, 100000, 0}, // mf 12
//            {-0, 100000, 0}, // TIC 13
//            {-0, 100000, 0}, // mf 14
//            {-0, 100000, 0}, // TIC 15
//            {-0, 100000, 0}, // mf 16
//    };
//    public final static double COLUMN_DENSE_INTERVALS[][][] = {
//            {{300, 1000, 60}, {50000, 70000, 90}}, // GTT 1 a
//            {{1500, 1700, 60}, {2500, 2550, 90}},// GTn 2 b
//            {{7000, 7200, 60}, {8000, 8200, 90}},// GGn 3 c
//            {{10, 30, 60}, {450, 500, 90}},// Ts 4 d
//            {{10, 30, 60}, {450, 500, 90}},// Tp 5 e
//            {{500, 550, 60}, {700, 720, 90}},// T48 6 f
//            {{600, 620, 60}, {660, 690, 90}},// T2 7 g
//            {{6, 14, 60}, {18, 20, 90}},// P2 8 h
//            {{0, 20, 60}, {80, 84, 90}},// TIC 9 i
//            {{300, 1000, 60}, {40000, 55000, 90}},// mf 10 j
//            {{300, 8000, 60}, {55000, 70000, 90}},// mf 11 k
//            {{300, 1800, 60}, {50000, 70000, 90}},// mf 12 l
//            {{700, 1000, 60}, {55000, 67000, 90}},// mf 13 m
//            {{3000, 10000, 60}, {50000, 70000, 90}},// mf 14 n
//            {{300, 1000, 60}, {40000, 64700, 90}},// mf 15 o
//            {{5800, 8000, 60}, {25000, 36000, 90}},// mf 16 p
//
//    };


    private int NR_OF_QUERIES; // ToDo: Null-pointer exception if very small
    private int MAX_DUPLICATES;

    private Random rand;
    private RandomDataGenerator randData;

    private static final int COMPOSITE_PROBABILITY = 5;

    private String TABLE_NAME;
    private double COLUMN_DENSE_INTERVALS[][][];
    private double COLUMN_MIN_MAX[][];
    private String COLUMN_LABELS[];

    QueryGenerator(double cdi[][][], double cmm[][], String cl[], String tn){
        COLUMN_DENSE_INTERVALS = cdi;
        COLUMN_MIN_MAX = cmm;
        COLUMN_LABELS = cl;
        TABLE_NAME = tn;

        NR_OF_QUERIES = 0;
        MAX_DUPLICATES = 0;
    }

    public QueryGenerator setNrOfQueries(int n){
        this.NR_OF_QUERIES = n;
        return this;
    }

    public QueryGenerator setMaxDuplicates(int m){
        this.MAX_DUPLICATES = m;
        return this;
    }

    public void generateBatchOfQueries(String filename){
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
            randData = new RandomDataGenerator();

            StringBuilder tempStmt = new StringBuilder();
            for (int k = 0; k < NR_OF_QUERIES; k++) {

                tempStmt.setLength(0);
                tempStmt.append(sqlPrefix);

                int selectedColumn = 0;
                selectedColumn = rand.nextInt((COLUMN_LABELS.length - selectedColumn)) + selectedColumn;
                generatePredicate(tempStmt, selectedColumn);
                localNrOfPredicates = 1;

                int compositeColumns = (int) randData.nextPoisson(2);
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

    private static final double INTERVAL_PROBABILITY = 0.5;
    private void generatePredicate(StringBuilder tempStmt, int selectedColumn){

        if (rand.nextDouble() > INTERVAL_PROBABILITY) {
            double start = 0;
            double end = 0;

            int random = ThreadLocalRandom.current().nextInt(100);
            boolean success = false;

            for (double[] denseInterval : COLUMN_DENSE_INTERVALS[selectedColumn]) {
                if (!success && random <= denseInterval[2]){
                    success = true;
                    if(COLUMN_MIN_MAX[selectedColumn][2] == 1) {
                        start = ThreadLocalRandom.current().nextDouble(denseInterval[1] + 1.0 - denseInterval[0]) + denseInterval[0];
                        end = ThreadLocalRandom.current().nextDouble(denseInterval[1] + 1.0 - start) + start;
                    }
                    else {
                        start = ThreadLocalRandom.current().nextInt((int) (denseInterval[1] + 1.0 - denseInterval[0])) + (int) denseInterval[0];
                        end = ThreadLocalRandom.current().nextInt((int) (denseInterval[1] + 1.0 - start)) + start;
                    }
                }
            }

            if(!success) {
                if (COLUMN_MIN_MAX[selectedColumn][2] == 1) {
                    start = ThreadLocalRandom.current().nextDouble((COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - COLUMN_MIN_MAX[selectedColumn][0])) + COLUMN_MIN_MAX[selectedColumn][0];
                    end = ThreadLocalRandom.current().nextDouble((COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - start)) + start;
                }
                else {
                    start = ThreadLocalRandom.current().nextInt((int) (COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - COLUMN_MIN_MAX[selectedColumn][0])) + (int) COLUMN_MIN_MAX[selectedColumn][0];
                    end = ThreadLocalRandom.current().nextInt((int) (COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - start)) + start;
                }
            }

            tempStmt.append(COLUMN_LABELS[selectedColumn])
                    .append(" < ")
                    .append(COLUMN_MIN_MAX[selectedColumn][2] == 1 ? start : String.valueOf((int) start))
                    .append(" AND ")
                    .append(COLUMN_MIN_MAX[selectedColumn][2] == 1 ? end : String.valueOf((int) end))
                    .append(" < ")
                    .append(COLUMN_LABELS[selectedColumn]);

        } else {
            double start = 0;

            int random = ThreadLocalRandom.current().nextInt(100);
            boolean success = false;

            for (double[] denseInterval : COLUMN_DENSE_INTERVALS[selectedColumn]) {
                if (!success && random <= denseInterval[2]){
                    success = true;
                    if(COLUMN_MIN_MAX[selectedColumn][2] == 1) {
                        start = ThreadLocalRandom.current().nextDouble((denseInterval[1] + 1.0 - denseInterval[0])) + denseInterval[0];
                    }
                    else {
                        start = ThreadLocalRandom.current().nextInt((int) (denseInterval[1] + 1.0 - denseInterval[0])) + (int) denseInterval[0];
                    }
                }
            }
            if(!success) {
                if(COLUMN_MIN_MAX[selectedColumn][2] == 1) {
                    start = ThreadLocalRandom.current().nextDouble((COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - COLUMN_MIN_MAX[selectedColumn][0])) + COLUMN_MIN_MAX[selectedColumn][0];
                }
                else {
                    start = ThreadLocalRandom.current().nextInt((int) (COLUMN_MIN_MAX[selectedColumn][1] + 1.0 - COLUMN_MIN_MAX[selectedColumn][0])) + (int) COLUMN_MIN_MAX[selectedColumn][0];
                }
            }

            tempStmt.append(COLUMN_LABELS[selectedColumn])
                    .append(" = ")
                    .append(COLUMN_MIN_MAX[selectedColumn][2] == 1 ? start : String.valueOf((int) start));
        }
    }
}
