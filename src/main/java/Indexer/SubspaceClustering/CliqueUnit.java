package Indexer.SubspaceClustering;

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.clique.CLIQUEInterval;
import de.lmu.ifi.dbs.elki.database.ids.*;

import java.util.ArrayList;
import java.util.Iterator;


public class CliqueUnit<V extends MyVector> {
    /**
     * The one-dimensional intervals of which this unit is build.
     */
    private ArrayList<CLIQUEInterval> intervals;

    /**
     * The ids of the feature vectors this unit contains.
     */
//    private ModifiableDBIDs ids;
//    private List<V> featuredVectors;

    /**
     * Flag that indicates if this unit is already assigned to a cluster.
     */
    private boolean assigned;

    private int coverage;

    /**
     * Creates a new k-dimensional unit for the given intervals.
     *
     * @param intervals the intervals belonging to this unit
     */
    private CliqueUnit(ArrayList<CLIQUEInterval> intervals, int c) {
        this.intervals = intervals;
        assigned = false;
        this.coverage = c;
//        this.featuredVectors = new LinkedList<>();
    }

    /**
     * Creates a new one-dimensional unit for the given interval.
     *
     * @param interval the interval belonging to this unit
     */
    public CliqueUnit(CLIQUEInterval interval) {
        intervals = new ArrayList<>();
        intervals.add(interval);
        assigned = false;
        this.coverage = 0;
//        this.featuredVectors = new LinkedList<>();
    }

    /**
     * Returns true, if the intervals of this unit contain the specified feature
     * vector.
     *
     * @param vector the feature vector to be tested for containment
     * @return true, if the intervals of this unit contain the specified feature
     *         vector, false otherwise
     */
    public boolean contains(V vector) {
        return vector.isContained(getIntervals());
    }

    /**
     * Adds the id of the specified feature vector to this unit, if this unit
     * contains the feature vector.
     *
     * @param id Vector id
     * @param vector the feature vector to be added
     * @return true, if this unit contains the specified feature vector, false
     *         otherwise
     */
    public boolean addFeatureVector(DBIDRef id, V vector) {
        if(contains(vector)) {
//            ids.add(id);
//            featuredVectors.add(vector);
            coverage++;
            return true;
        }
        return false;
    }

    public boolean addFeatureVector(V vector, int amount) {
        if(contains(vector)) {
            coverage+=amount;
            return true;
        }
        return false;
    }

    /**
     * Returns the number of feature vectors this unit contains.
     *
     * @return the number of feature vectors this unit contains
     */
    public int numberOfFeatureVectors() {
//        return featuredVectors.size();
        return coverage;
    }

    /**
     * Returns the selectivity of this unit, which is defined as the fraction of
     * total feature vectors contained in this unit.
     *
     * @param total the total number of feature vectors
     * @return the selectivity of this unit
     */
    public double selectivity(double total) {
//        return ((double)featuredVectors.size()) / total;
        return ((double)coverage) / total;
    }

    /**
     * Returns a sorted set of the intervals of which this unit is build.
     *
     * @return a sorted set of the intervals of which this unit is build
     */
    public ArrayList<CLIQUEInterval> getIntervals() {
        return intervals;
    }

    /**
     * Returns the interval of the specified dimension.
     *
     * @param dimension the dimension of the interval to be returned
     * @return the interval of the specified dimension
     */
    public CLIQUEInterval getInterval(int dimension) {
        // TODO: use binary search instead?
        for(CLIQUEInterval i : intervals) {
            if(i.getDimension() == dimension) {
                return i;
            }
        }
        return null;
    }

    /**
     * Returns true if this unit contains the left neighbor of the specified
     * interval.
     *
     * @param i the interval
     * @return true if this unit contains the left neighbor of the specified
     *         interval, false otherwise
     */
    public boolean containsLeftNeighbor(CLIQUEInterval i) {
        CLIQUEInterval interval = getInterval(i.getDimension());
        return (interval != null) && (interval.getMax() == i.getMin());
    }

    public boolean containsLeftNeighbor(ArrayList<CLIQUEInterval> i, int dim) {


        for(int index = 0; index < intervals.size(); index++){
            if(intervals.get(index).getMin() != i.get(index).getMin()){
                if(index != dim || intervals.get(index).getMax() != i.get(index).getMin()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns true if this unit contains the right neighbor of the specified
     * interval.
     *
     * @param i the interval
     * @return true if this unit contains the right neighbor of the specified
     *         interval, false otherwise
     */
    public boolean containsRightNeighbor(CLIQUEInterval i) {
        CLIQUEInterval interval = getInterval(i.getDimension());
        return (interval != null) && (interval.getMin() == i.getMax());
    }

    public boolean containsRightNeighbor(ArrayList<CLIQUEInterval> i, int dim) {
        for(int index = 0; index < intervals.size(); index++){
            if(intervals.get(index).getMin() != i.get(index).getMin()){
                if(index != dim || intervals.get(index).getMin() != i.get(index).getMax()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns true if this unit is already assigned to a cluster.
     *
     * @return true if this unit is already assigned to a cluster, false
     *         otherwise.
     */
    public boolean isAssigned() {
        return assigned;
    }

    /**
     * Marks this unit as assigned to a cluster.
     */
    public void markAsAssigned() {
        this.assigned = true;
    }

    /**
     * Returns the ids of the feature vectors this unit contains.
     *
     * @return the ids of the feature vectors this unit contains
     */
//    public DBIDs getIds() {
//        return ids;
//    }

    /**
     * Joins this unit with the specified unit.
     *
     * @param other the unit to be joined
     * @param all the overall number of feature vectors
     * @param tau the density threshold for the selectivity of a unit
     * @return the joined unit if the selectivity of the join result is equal or
     *         greater than tau, null otherwise
     */
    public CliqueUnit<V> join(CliqueUnit<V> other, double all, double tau) {
        CLIQUEInterval i1 = this.intervals.get(this.intervals.size() - 1);
        CLIQUEInterval i2 = other.intervals.get(other.intervals.size() - 1);
        if(i1.getDimension() >= i2.getDimension()) {
            return null;
        }

        Iterator<CLIQUEInterval> it1 = this.intervals.iterator();
        Iterator<CLIQUEInterval> it2 = other.intervals.iterator();
        ArrayList<CLIQUEInterval> resultIntervals = new ArrayList<>();
        for(int i = 0; i < this.intervals.size() - 1; i++) {
            i1 = it1.next();
            i2 = it2.next();
            if(!i1.equals(i2)) {
                return null;
            }
            resultIntervals.add(i1);
        }
        resultIntervals.add(this.intervals.get(this.intervals.size() - 1));
        resultIntervals.add(other.intervals.get(other.intervals.size() - 1));

//        HashSetModifiableDBIDs resultIDs = DBIDUtil.newHashSet(this.ids);
//        resultIDs.retainAll(other.ids);

        // ToDo: Is this necessary?
//        if(((double)(this.coverage + other.coverage)) / all >= tau) {
//            return new ExtendedCliqueUnit<>(resultIntervals, (this.coverage + other.coverage));
//        }

        // ToDo: Is the coverage even needed at this point, considering that it is reset? And should it not be equal to the minimum of the two?
        return new CliqueUnit<>(resultIntervals, (this.coverage + other.coverage));
    }

    /**
     * Returns a string representation of this unit that contains the intervals of
     * this unit.
     *
     * @return a string representation of this unit
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(CLIQUEInterval interval : intervals) {
            result.append(interval).append(' ');
        }

        return result.toString();
    }
}
