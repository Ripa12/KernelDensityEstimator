package interval_tree.SubspaceClustering;

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.clique.CLIQUEInterval;
import interval_tree.SubspaceClustering.MyVector;

import java.util.ArrayList;

public class MyInterval implements MyData {

    private double upperBound;
    private double lowerBound;

    public MyInterval(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public double getLow() {
        return lowerBound;
    }

    @Override
    public double getHigh() {
        return upperBound;
    }

    @Override
    public boolean isContained(CLIQUEInterval interval) {

        if(interval.getMin() > upperBound || lowerBound >= interval.getMax()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isContained(double min, double max) { // ToDo: Should change name from isContained to isOverlapping
        if(min > upperBound || lowerBound >= max) {
            return false;
        }
        return true;
    }

    @Override
    public boolean contains(double min, double max) {
        return (lowerBound >= min && max >= upperBound);
    }
}
