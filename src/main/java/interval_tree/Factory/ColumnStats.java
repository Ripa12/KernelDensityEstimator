package interval_tree.Factory;

/**
 * Created by Richard on 2018-06-23.
 */
public class ColumnStats {
    public int support;
    public double minQuery, maxQuery;

    ColumnStats(int s, double min, double max) {
        support = s;
        minQuery = min;
        maxQuery = max;
    }

    public ColumnStats() {
        support = 0;
        minQuery = Double.MAX_VALUE;
        maxQuery = Double.MIN_VALUE;
    }

    public void addStat(double min, double max){
        support++;
        minQuery = Math.min(min, minQuery);
        maxQuery = Math.max(max, maxQuery);
    }
}
