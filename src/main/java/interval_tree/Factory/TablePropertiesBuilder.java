package interval_tree.Factory;

import interval_tree.FrequentPatternMining.SupportCount.TableCount;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Richard on 2018-06-04.
 */
public class TablePropertiesBuilder {

    private String columnLabels[];
    private double columnMinMax[][];
    private double columnDenseIntervals[][][];

    private double avgDenseClusters;
    private int denseColumnProb;

    private TableCount tb;

    private TreeSet<Integer> removeItems;

    public TablePropertiesBuilder(TableCount tb) {
        this.tb = tb;

        this.avgDenseClusters = 0.0;
        this.denseColumnProb = 0;
    }

    public TablePropertiesBuilder setAvgDenseClustes(double c) {
        avgDenseClusters = c;
        return this;
    }

    public TablePropertiesBuilder setDenseColumnProb(int d) {
        denseColumnProb = d;
        return this;
    }

    public QueryGenerator build(String source, String tableName) {
        if (tb == null)
            return null;

        removeItems = new TreeSet<>();
        initialize(source);

        columnDenseIntervals = new double[columnMinMax.length][][];

        RandomDataGenerator rand = new RandomDataGenerator();

        for (int i = 0; i < columnMinMax.length; i++) {
            int percentage = rand.nextInt(0, 100);
            if (denseColumnProb > percentage) {
                int denseClusters = (int) rand.nextExponential(avgDenseClusters);
                columnDenseIntervals[i] = new double[denseClusters][3];

                int[] distribution = new int[denseClusters];
                int start = 0;
                for (int j = 0; j < denseClusters; j++) {
                    distribution[j] = rand.nextInt(start, 90);
                    start = distribution[j];
                }
                Arrays.sort(distribution);


                double val = columnMinMax[i][0];
                if (columnMinMax[i][2] == 1) {
                    for (int j = 0; j < denseClusters; j++) {
                        columnDenseIntervals[i][j][0] = val = val < columnMinMax[i][1] ?
                                rand.nextUniform(val, columnMinMax[i][1]) : val;
                        columnDenseIntervals[i][j][1] = val < columnMinMax[i][1] ?
                                rand.nextUniform(val, columnMinMax[i][1]) : val;
                        val = columnDenseIntervals[i][j][1];
                        columnDenseIntervals[i][j][2] = distribution[j];
                    }
                } else {
                    for (int j = 0; j < denseClusters; j++) {
                        columnDenseIntervals[i][j][0] = val = (int) (val < columnMinMax[i][1] ?
                                rand.nextUniform(val, columnMinMax[i][1]) : val);
                        columnDenseIntervals[i][j][1] = (int) (val < columnMinMax[i][1] ?
                                rand.nextUniform(val, columnMinMax[i][1]) : val);
                        val = columnDenseIntervals[i][j][1];
                        columnDenseIntervals[i][j][2] = distribution[j];
                    }
                }
            } else {
                columnDenseIntervals[i] = new double[0][0];
            }
        }

        Iterator it = removeItems.descendingIterator();
        while(it.hasNext()){
            Integer index = (int)it.next();

            columnLabels = ArrayUtils.remove(columnLabels, index);
            columnDenseIntervals = ArrayUtils.remove(columnDenseIntervals, index);
            columnMinMax = ArrayUtils.remove(columnMinMax, index);
        }


        tb.addColumns(tableName, columnLabels, columnMinMax);

        return new QueryGenerator(columnDenseIntervals, columnMinMax, columnLabels, tableName);
    }

    private void initialize(String source) {
        String sourcePath = "data/testdata/unittests/" + source;
        if (SystemUtils.IS_OS_WINDOWS) {
            sourcePath = sourcePath.replaceFirst("/", "//");
        }

        columnLabels = null;
        columnMinMax = null;

        try (BufferedReader br = new BufferedReader(new FileReader(sourcePath))) {

            String line = br.readLine();
            if (line != null) {
                columnLabels = line.split(",");
                columnMinMax = new double[columnLabels.length][3];
                for (double[] minMax : columnMinMax) {
                    minMax[0] = Double.MAX_VALUE;
                    minMax[1] = Double.MIN_VALUE;
                    minMax[2] = 0;
                }
            }

            while ((line = br.readLine()) != null) {
                String[] elements = line.split(",");
                for (int i = 0; i < elements.length; i++) {
                    if (isInteger(elements[i])) {
                        columnMinMax[i][0] = Math.min(columnMinMax[i][0], Integer.parseInt(elements[i]));
                        columnMinMax[i][1] = Math.max(columnMinMax[i][1], Integer.parseInt(elements[i]));
                    } else if (isDouble(elements[i])) {
                        columnMinMax[i][0] = Math.min(columnMinMax[i][0], Double.parseDouble(elements[i]));
                        columnMinMax[i][1] = Math.max(columnMinMax[i][1], Double.parseDouble(elements[i]));
                        columnMinMax[i][2] = 1;
                    } else if (!elements[i].equals("?")) {
                        removeItems.add(i);
                    }
                }
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
