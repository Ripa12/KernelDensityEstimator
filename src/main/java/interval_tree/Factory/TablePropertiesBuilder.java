package interval_tree.Factory;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Richard on 2018-06-04.
 */
public class TablePropertiesBuilder {

    String columnLabels[];
    double columnMinMax[][];
    double column_dense_intervals[][][];

    public TablePropertiesBuilder(String source){
        String sourcePath = "data/testdata/unittests/TableProperties" + source;
        if (SystemUtils.IS_OS_WINDOWS) {
            sourcePath = sourcePath.replaceFirst("/", "//");
        }

        columnLabels = null;
        columnMinMax = null;

        try(BufferedReader br = new BufferedReader(new FileReader(sourcePath))){

            String line = br.readLine();
            if(line != null){
                columnLabels = line.split(",");
                columnMinMax = new double[columnLabels.length][3];
                for (double[] minMax : columnMinMax) {
                    minMax[0] = Double.MAX_VALUE;
                    minMax[1] = Double.MIN_VALUE;
                    minMax[2] = 0;
                }
            }

            while(line != null) {
                String[] elements = line.split(",");
                for (int i = 0; i < elements.length; i++) {
                    if(isInteger(elements[i])) {
                        columnMinMax[i][0] = Math.min(columnMinMax[i][0], Integer.parseInt(elements[i]));
                        columnMinMax[i][1] = Math.max(columnMinMax[i][1], Integer.parseInt(elements[i]));
                    }
                    else if(isDouble(elements[i])) {
                        columnMinMax[i][0] = Math.min(columnMinMax[i][0], Double.parseDouble(elements[i]));
                        columnMinMax[i][1] = Math.max(columnMinMax[i][1], Double.parseDouble(elements[i]));
                        columnMinMax[i][1] = 1;
                    }
                }
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void build(double avgDenseClusters ){
        column_dense_intervals = null;



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
