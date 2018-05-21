package interval_tree.SubspaceClustering;

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.clique.CLIQUEInterval;
import interval_tree.SubspaceClustering.MyVector;

import java.util.ArrayList;

public class MyPoint implements MyData {

    double value;

    public MyPoint(double v) {
        value = v;
    }

    @Override
    public double getLow() {
        return value;
    }

    @Override
    public double getHigh() {
        return value;
    }

    @Override
    public boolean isContained(CLIQUEInterval interval) {

        if(interval.getMin() > value || value >= interval.getMax()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isContained(double min, double max) {
        if(min > value || value >= max) {
            return false;
        }
        return true;
    }

    @Override
    public boolean contains(double min, double max) {
        if(min > value || value >= max) {
            return false;
        }
        return true;
    }
}
