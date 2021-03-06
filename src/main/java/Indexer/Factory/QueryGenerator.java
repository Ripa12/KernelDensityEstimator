package Indexer.Factory;


import Indexer.GUI.Settings;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static Indexer.Utility.getOutputDirectory;


/**
 * Created by Richard on 2018-03-27.
 */
public class QueryGenerator {

    private int nrOfQueries; // ToDo: Null-pointer exception if very small
    private double avgNrOfDuplicates;
    private double avgNrOfColumns;

    private Random rand;
    private RandomDataGenerator randMean; // ToDo: Combine Random and RandomDataGenerator

    private String tableName;
    private double columnDenseIntervals[][][];
    private double columnMinMax[][];
    private String columnLabels[];

    QueryGenerator(double cdi[][][], double cmm[][], String cl[], String tn){
        columnDenseIntervals = cdi;
        columnMinMax = cmm;
        columnLabels = cl;
        tableName = tn;

        nrOfQueries = 0;
        avgNrOfDuplicates = 0;
        avgNrOfColumns = 0;

        randMean = new RandomDataGenerator();
        rand = new Random();

    }

    public QueryGenerator setNrOfQueries(int n){
        this.nrOfQueries = n;
        return this;
    }

    public QueryGenerator setAverageNrOfDuplicates(double m){
        this.avgNrOfDuplicates = m;
        return this;
    }

    public QueryGenerator setAverageNrOfCompositeColumns(double c){
        this.avgNrOfColumns = c;
        return this;
    }

    public void generateBatchOfQueries(String filename){

        String sqlPrefix = "SELECT * FROM "+tableName+" WHERE ";

        PrintWriter out = null;
        int k = 0;
        try {
            out = new PrintWriter(new OutputStreamWriter(
                    new BufferedOutputStream(
                            new FileOutputStream(
                                    new File(getOutputDirectory() + filename))), "UTF-8"));

            StringBuilder tempStmt = new StringBuilder();
            for (; k <= nrOfQueries;) {

                tempStmt.setLength(0);
                tempStmt.append(sqlPrefix);

                List<Integer> selectedColumns = new LinkedList<>();

                int[] columnIndexes = IntStream.rangeClosed(0, columnLabels.length - 1).toArray();
                int selectedColumn;

                int compositeColumns = (int)avgNrOfColumns;
                while (compositeColumns > 0 && columnIndexes.length > 0) {
                    compositeColumns--;

                    selectedColumns.add(selectedColumn = columnIndexes[rand.nextInt(columnIndexes.length - 1)]);
                    columnIndexes = ArrayUtils.removeElement(columnIndexes, selectedColumn);
                }

                generateStatement(tempStmt, selectedColumns);

                out.print(tempStmt.toString());

                int duplicates = (int) randMean.nextPoisson(avgNrOfDuplicates);
                for (int t = 0; t < duplicates; t++) {
                    tempStmt.setLength(0);
                    tempStmt.append(sqlPrefix);

                    generateStatement(tempStmt, selectedColumns);

                    out.print(tempStmt.toString());
                }
                k += duplicates + 1;
            }
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        } finally {
        if(out != null) {
            out.flush();
            out.close();
        }
    }
        System.out.println("NrOfQueries: " + k);
    }

    private void generateStatement(StringBuilder tempStmt, List<Integer> selectedColumns){

        generatePredicate(tempStmt, selectedColumns.get(0));

        for (int i = 1; i < selectedColumns.size(); i++) {
            tempStmt.append(" AND ");
            generatePredicate(tempStmt, selectedColumns.get(i));
        }

        tempStmt.append(";\n");
    }

    private static final double INTERVAL_PROBABILITY = 0.5;
    private void generatePredicate(StringBuilder tempStmt, int selectedColumn){

        if (rand.nextDouble() > INTERVAL_PROBABILITY) {
            double start = 0;
            double end = 0;

            int random = ThreadLocalRandom.current().nextInt(100);
            boolean success = false;

            for (double[] denseInterval : columnDenseIntervals[selectedColumn]) {
                if (!success && random <= denseInterval[2]){
                    success = true;
                    if(columnMinMax[selectedColumn][2] == 1) {
//                        start = ThreadLocalRandom.current().nextDouble(denseInterval[1] + 1.0 - denseInterval[0]) + denseInterval[0];
//                        end = ThreadLocalRandom.current().nextDouble(denseInterval[1] + 1.0 - start) + start;
//                        start = denseInterval[0] + (denseInterval[1] - denseInterval[0]) * rand.nextDouble();
//                        end = (denseInterval[1] - denseInterval[0]) / intervalFraction;
//                        end = Math.min(end, denseInterval[1]);

                        start = denseInterval[0];
                        end = (denseInterval[1]);
                    }
                    else {
//                        start = ThreadLocalRandom.current().nextInt((int) (denseInterval[1] + 1.0 - denseInterval[0])) + (int) denseInterval[0];
////                        end = ThreadLocalRandom.current().nextInt((int) (denseInterval[1] + 1.0 - start)) + start;
//                        end = (denseInterval[1] - denseInterval[0]) / intervalFraction;
//                        end = (int) Math.min(end, denseInterval[1]);

                        start = (int)denseInterval[0];
                        end = (int)(denseInterval[1]);
                    }
                }
            }

            if(!success) {
                if (columnMinMax[selectedColumn][2] == 1) {
//                    start = ThreadLocalRandom.current().nextDouble((columnMinMax[selectedColumn][1] + 1.0 - columnMinMax[selectedColumn][0])) + columnMinMax[selectedColumn][0];
//                    end = ThreadLocalRandom.current().nextDouble((columnMinMax[selectedColumn][1] + 1.0 - start)) + start;
                    start = columnMinMax[selectedColumn][0] + (columnMinMax[selectedColumn][1] - columnMinMax[selectedColumn][0]) * rand.nextDouble();
                    end = start + (columnMinMax[selectedColumn][1] - start) * rand.nextDouble();

                }
                else {
                    start = ThreadLocalRandom.current().nextInt((int) (columnMinMax[selectedColumn][1] + 1.0 - columnMinMax[selectedColumn][0])) + (int) columnMinMax[selectedColumn][0];
                    end = ThreadLocalRandom.current().nextInt((int) (columnMinMax[selectedColumn][1] + 1.0 - start)) + start;
                }
            }

            tempStmt.append(columnLabels[selectedColumn])
                    .append(" > ") // ToDo: Should be > (I changed it but have not tested)
                    .append(columnMinMax[selectedColumn][2] == 1 ? start : String.valueOf((int) start))
                    .append(" AND ")
                    .append(columnMinMax[selectedColumn][2] == 1 ? end : String.valueOf((int) end))
                    .append(" > ")
                    .append(columnLabels[selectedColumn]);

        } else {
            double start = 0;

            int random = ThreadLocalRandom.current().nextInt(100);
            boolean success = false;

            for (double[] denseInterval : columnDenseIntervals[selectedColumn]) {
                if (!success && random <= denseInterval[2]){
                    success = true;
                    if(columnMinMax[selectedColumn][2] == 1) {
                        start = denseInterval[0] + (denseInterval[1] - denseInterval[0]) * rand.nextDouble();
                    }
                    else {
                        start = ThreadLocalRandom.current().nextInt((int) (denseInterval[1] + 1.0 - denseInterval[0])) + (int) denseInterval[0];
                    }
                }
            }
            if(!success) {
                if(columnMinMax[selectedColumn][2] == 1) {
                    start = columnMinMax[selectedColumn][0] + (columnMinMax[selectedColumn][1] - columnMinMax[selectedColumn][0]) * rand.nextDouble();
                }
                else {
                    start = ThreadLocalRandom.current().nextInt((int) (columnMinMax[selectedColumn][1] + 1.0 - columnMinMax[selectedColumn][0])) + (int) columnMinMax[selectedColumn][0];
                }
            }

            tempStmt.append(columnLabels[selectedColumn])
                    .append(" = ")
                    .append(columnMinMax[selectedColumn][2] == 1 ? start : String.valueOf((int) start));
        }
    }
}
