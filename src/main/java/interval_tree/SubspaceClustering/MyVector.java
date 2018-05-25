package interval_tree.SubspaceClustering;

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.clique.CLIQUEInterval;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;

import java.util.ArrayList;

/**
 * Created by Richard on 2018-04-22.
 */
public class MyVector implements NumberVector {

    private MyData[] data;

    public MyVector(MyData[] data){
        this.data = data;
    }

    public boolean contains(double min, double max, int dim) {
        return data[dim].contains(min, max);
    }

    public boolean isContained(ArrayList<CLIQUEInterval> intervals) {
        for (CLIQUEInterval interval : intervals) {
            if(!data[interval.getDimension()].isContained(interval)){
                return false;
            }
        }
        return true;
    }

    @Override
    public int getDimensionality() {
        return data.length;
    }

    @Override
    public double getMin(int i) {
        return data[i].getLow();
    }

    @Override
    public double getMax(int i) {
        return data[i].getHigh();
    }

    @Override
    public Number getValue(int i) {
        return data[i].getLow();
    }

    @Override
    public double doubleValue(int i) {
        return data[i].getLow();
    }

    @Override
    public float floatValue(int i) {
        return (float)data[i].getLow();
    }

    @Override
    public int intValue(int i) {
        return (int)data[i].getLow();
    }

    @Override
    public long longValue(int i) {
        return (long)data[i].getLow();
    }

    @Override
    public short shortValue(int i) {
        return (short)data[i].getLow();
    }

    @Override
    public byte byteValue(int i) {
        return (byte)data[i].getLow();
    }

    @Override
    public Vector getColumnVector() {
        return new Vector();
    }
}
