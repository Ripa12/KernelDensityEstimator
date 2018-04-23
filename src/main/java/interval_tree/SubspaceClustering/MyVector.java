package interval_tree.SubspaceClustering;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;

/**
 * Created by Richard on 2018-04-22.
 */
public class MyVector implements NumberVector {

    int[] vector;

    public MyVector(int[] v){
        this.vector = v;
    }

    @Override
    public int getDimensionality() {
        return vector.length;
    }

    @Override
    public double getMin(int i) {
        return 0;
    }

    @Override
    public double getMax(int i) {
        return 0;
    }

    @Override
    public Number getValue(int i) {
        return vector[i];
    }

    @Override
    public double doubleValue(int i) {
        return (double)vector[i];
    }

    @Override
    public float floatValue(int i) {
        return (float)vector[i];
    }

    @Override
    public int intValue(int i) {
        return vector[i];
    }

    @Override
    public long longValue(int i) {
        return (long)vector[i];
    }

    @Override
    public short shortValue(int i) {
        return (short)vector[i];
    }

    @Override
    public byte byteValue(int i) {
        return (byte)vector[i];
    }

    @Override
    public Vector getColumnVector() {
        return new Vector();
    }
}
