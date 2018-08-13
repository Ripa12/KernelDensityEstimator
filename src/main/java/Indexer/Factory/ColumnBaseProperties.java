package Indexer.Factory;

/**
 * Created by Richard on 2018-06-23.
 */
public class ColumnBaseProperties {
    double minBoundary, maxBoundary;
    boolean isDecimal;

    ColumnBaseProperties(double minVal, double maxVal, boolean t){
        this.minBoundary = minVal;
        this.maxBoundary = maxVal;

        isDecimal = t;
    }
}
