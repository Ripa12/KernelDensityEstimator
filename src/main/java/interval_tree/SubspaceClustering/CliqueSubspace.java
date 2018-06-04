package interval_tree.SubspaceClustering;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2015
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.clique.CLIQUEInterval;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.Subspace;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.utilities.BitsUtil;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;
import interval_tree.CandidateIndex.CompoundPartialIndex;
import interval_tree.CandidateIndex.PartialIndex;
import interval_tree.FrequentPatternMining.SupportCount.TableCount;

/**
 * Represents a subspace of the original data space in the CLIQUE algorithm.
 *
 * @param <V> the type of NumberVector this subspace contains
 * @author Elke Achtert
 * @apiviz.has CoverageComparator
 * @apiviz.composedOf CLIQUEUnit
 * @since 0.2
 */
public class CliqueSubspace<V extends MyVector> extends Subspace {
    /**
     * The dense units belonging to this subspace.
     */
    private List<CliqueUnit<V>> denseUnits;

    /**
     * The coverage of this subspace, which is the number of all feature vectors
     * that fall inside the dense units of this subspace.
     */
    private int coverage;

    private double[] lowerBounds;
    private double[] maximumBounds;

    boolean contains(MyVector other) {
        if (other.getDimensionality() != dimensionality())
            return false;

        final long[] dims = getDimensions();
        for (int dim = BitsUtil.nextSetBit(dims, 0); dim >= 0; dim = BitsUtil.nextSetBit(dims, dim + 1)) {
            if (!other.contains(lowerBounds[dim], maximumBounds[dim], dim)) {
                return false;
            }
        }

        coverage++;
        return true;
    }

    /**
     * Creates a new one-dimensional subspace of the original data space.
     *
     * @param dimension the dimension building this subspace
     */
    public CliqueSubspace(int dimension) {
        super(dimension);
        denseUnits = new ArrayList<>();
        coverage = 0;

        lowerBounds = new double[1];
        maximumBounds = new double[1];
        for (int i = 0; i < 1; i++) {
            lowerBounds[i] = Double.MAX_VALUE;
            maximumBounds[i] = Double.MIN_VALUE;
        }
    }

    /**
     * Creates a new k-dimensional subspace of the original data space.
     *
     * @param dimensions the dimensions building this subspace
     */
    public CliqueSubspace(long[] dimensions) {
        super(dimensions);
        denseUnits = new ArrayList<>();
        coverage = 0;

        lowerBounds = new double[BitsUtil.cardinality(dimensions)];
        maximumBounds = new double[BitsUtil.cardinality(dimensions)];
        for (int i = 0; i < lowerBounds.length; i++) {
            lowerBounds[i] = Double.MAX_VALUE;
            maximumBounds[i] = Double.MIN_VALUE;
        }
    }

    public void resetCoverage() {
        this.coverage = 0;
    }

    // ToDo: Temporary logger
    public void outputBounds() {
        for (int i = 0; i < lowerBounds.length; i++) {
            System.out.println(i + "_[" + lowerBounds[i] + ", " + maximumBounds[i] + "]");
        }
    }

    public CompoundPartialIndex makePartialIndex(String table, List<String> columns,
                                                 double[] negativeInfinity, double[] positiveInfinity, TableCount tc) {
        // ToDo: Coverage should be in Composite class or Full index instead
        CompoundPartialIndex compoundPartialIndex = new CompoundPartialIndex(table);

        for (int i = 0; i < lowerBounds.length; i++) {
            if (negativeInfinity[i] <= lowerBounds[i] && positiveInfinity[i] >= maximumBounds[i]) {
                CompoundPartialIndex.Predicate tempPredicate = new CompoundPartialIndex.Predicate();

                if(negativeInfinity[i] >= lowerBounds[i]){
                    tempPredicate.addPartialIndex(new PartialIndex(coverage, 0, table, columns.get(i),
                            tc.getCorrectType(table, columns.get(i), maximumBounds[i]),
                            PartialIndex.ConditionType.LESS_THAN));
                }
                else if(positiveInfinity[i] <= maximumBounds[i]){
                    tempPredicate.addPartialIndex(new PartialIndex(coverage, 0, table, columns.get(i),
                            tc.getCorrectType(table, columns.get(i), lowerBounds[i]), PartialIndex.ConditionType.GREATER_THAN));
                }
                else {
                    tempPredicate.addPartialIndex(new PartialIndex(coverage, 0, table, columns.get(i),
                            tc.getCorrectType(table, columns.get(i), lowerBounds[i]),
                            tc.getCorrectType(table, columns.get(i), maximumBounds[i])));
                }
                compoundPartialIndex.addCompoundPredicate(tempPredicate);
            }
        }

        return compoundPartialIndex;
    }

    /**
     * Adds the specified dense unit to this subspace.
     *
     * @param unit the unit to be added.
     */
    public void addDenseUnit(CliqueUnit<V> unit) {
        Collection<CLIQUEInterval> intervals = unit.getIntervals();

        int k = 0;
        for (CLIQUEInterval interval : intervals) {
            if (!BitsUtil.get(getDimensions(), interval.getDimension())) {
                throw new IllegalArgumentException("Unit " + unit + "cannot be added to this subspace, because of wrong dimensions!");
            }

            lowerBounds[k] = Math.min(interval.getMin(), lowerBounds[k]);
            maximumBounds[k] = Math.max(interval.getMax(), maximumBounds[k]);
            k++;
        }

        getDenseUnits().add(unit);
        coverage += unit.numberOfFeatureVectors();
    }

    /**
     * Determines all clusters in this subspace by performing a depth-first search
     * algorithm to find connected dense units.
     *
     * @return the clusters in this subspace and the corresponding cluster models
     */
    public List<Pair<Subspace, ModifiableDBIDs>> determineClusters() {
        List<Pair<Subspace, ModifiableDBIDs>> clusters = new ArrayList<>();

        for (CliqueUnit<V> unit : getDenseUnits()) {
            if (!unit.isAssigned()) {
                ModifiableDBIDs cluster = DBIDUtil.newHashSet();
                CliqueSubspace<V> model = new CliqueSubspace<>(getDimensions());
                clusters.add(new Pair<Subspace, ModifiableDBIDs>(model, cluster));
                dfs(unit, cluster, model);

                model.coverage = 0; // ToDo: Nicer solution?
            }
        }
        return clusters;
    }

    /**
     * Depth-first search algorithm to find connected dense units in this subspace
     * that build a cluster. It starts with a unit, assigns it to a cluster and
     * finds all units it is connected to.
     *
     * @param unit    the unit
     * @param cluster the IDs of the feature vectors of the current cluster
     * @param model   the model of the cluster
     */
    public void dfs(CliqueUnit<V> unit, ModifiableDBIDs cluster, CliqueSubspace<V> model) {
//        cluster.addDBIDs(unit.getIds());
        unit.markAsAssigned();
        model.addDenseUnit(unit); // 17.50 && 103

        final long[] dims = getDimensions();
        for (int dim = BitsUtil.nextSetBit(dims, 0); dim >= 0; dim = BitsUtil.nextSetBit(dims, dim + 1)) {
//            ExtendedCliqueUnit<V> left = leftNeighbor(unit, dim);
            CliqueUnit<V> left = leftAdjacentNeighbor(unit, dim);
            if (left != null) {// && !left.isAssigned()) {
                dfs(left, cluster, model);
            }

//            ExtendedCliqueUnit<V> right = rightNeighbor(unit, dim);
            CliqueUnit<V> right = rightAdjacentNeighbor(unit, dim);
            if (right != null) {// && !right.isAssigned()) {
                dfs(right, cluster, model);
            }
        }
    }

    /**
     * Returns the left neighbor of the given unit in the specified dimension.
     *
     * @param unit the unit to determine the left neighbor for
     * @param dim  the dimension
     * @return the left neighbor of the given unit in the specified dimension
     */
    public CliqueUnit<V> leftNeighbor(CliqueUnit<V> unit, int dim) {
        CLIQUEInterval i = unit.getInterval(dim);

        for (CliqueUnit<V> u : getDenseUnits()) {
            if (!u.isAssigned() && u.containsLeftNeighbor(i)) {
                return u;
            }
        }
        return null;
    }

    public CliqueUnit<V> leftAdjacentNeighbor(CliqueUnit<V> unit, int dim) {
        for (CliqueUnit<V> u : getDenseUnits()) {
            if (!u.isAssigned() && u.containsLeftNeighbor(unit.getIntervals(), dim)) {
                return u;
            }
        }
        return null;
    }


    /**
     * Returns the right neighbor of the given unit in the specified dimension.
     *
     * @param unit the unit to determine the right neighbor for
     * @param dim  the dimension
     * @return the right neighbor of the given unit in the specified dimension
     */
    public CliqueUnit<V> rightNeighbor(CliqueUnit<V> unit, Integer dim) {
        CLIQUEInterval i = unit.getInterval(dim);

        for (CliqueUnit<V> u : getDenseUnits()) {
            if (u.containsRightNeighbor(i) && !u.isAssigned()) {
                return u;
            }
        }
        return null;
    }

    public CliqueUnit<V> rightAdjacentNeighbor(CliqueUnit<V> unit, Integer dim) {
        for (CliqueUnit<V> u : getDenseUnits()) {
            if (!u.isAssigned() && u.containsRightNeighbor(unit.getIntervals(), dim)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Returns the coverage of this subspace, which is the number of all feature
     * vectors that fall inside the dense units of this subspace.
     *
     * @return the coverage of this subspace
     */
    public int getCoverage() {
        return coverage;
    }

    /**
     * @return the denseUnits
     */
    public List<CliqueUnit<V>> getDenseUnits() {
        return denseUnits;
    }

    public void setDenseUnits(List<CliqueUnit<V>> v) {
        denseUnits = v;
    }

    /**
     * Joins this subspace and its dense units with the specified subspace and its
     * dense units. The join is only successful if both subspaces have the first
     * k-1 dimensions in common (where k is the number of dimensions) and the last
     * dimension of this subspace is less than the last dimension of the specified
     * subspace.
     *
     * @param other the subspace to join
     * @param all   the overall number of feature vectors
     * @param tau   the density threshold for the selectivity of a unit
     * @return the join of this subspace with the specified subspace if the join
     * condition is fulfilled, null otherwise.
     * @see de.lmu.ifi.dbs.elki.data.Subspace#joinLastDimensions
     */
    public CliqueSubspace<V> join(CliqueSubspace<V> other, double all, double tau) {
        long[] dimensions = joinLastDimensions(other);
        if (dimensions == null) {
            return null;
        }

        CliqueSubspace<V> s = new CliqueSubspace<>(dimensions);
        for (CliqueUnit<V> u1 : this.getDenseUnits()) {
            for (CliqueUnit<V> u2 : other.getDenseUnits()) {
                CliqueUnit<V> u = u1.join(u2, all, tau);
                if (u != null) {
                    s.addDenseUnit(u);
                }
            }
        }
        if (s.getDenseUnits().isEmpty()) {
            return null;
        }
        return s;
    }

    /**
     * Calls the super method and adds additionally the coverage, and the dense
     * units of this subspace.
     */
    @Override
    public String toString(String pre) {
        StringBuilder result = new StringBuilder();
        result.append(super.toString(pre));
        result.append('\n').append(pre).append("Coverage: ").append(coverage);
        result.append('\n').append(pre).append("Units: " + "\n");
        for (CliqueUnit<V> denseUnit : getDenseUnits()) {
//            result.append(pre).append("   ").append(denseUnit.toString()).append("   ").append(denseUnit.getIds().size()).append(" objects\n");
        }
        return result.toString();
    }

    /**
     * A partial comparator for CLIQUESubspaces based on their coverage. The
     * CLIQUESubspaces are reverse ordered by the values of their coverage.
     * <p>
     * Note: this comparator provides an ordering that is inconsistent with
     * equals.
     *
     * @author Elke Achtert
     */
    public static class CoverageComparator implements Comparator<CliqueSubspace<?>> {
        /**
         * Compares the two specified CLIQUESubspaces for order. Returns a negative
         * integer, zero, or a positive integer if the coverage of the first
         * subspace is greater than, equal to, or less than the coverage of the
         * second subspace. I.e. the subspaces are reverse ordered by the values of
         * their coverage.
         * <p>
         * Note: this comparator provides an ordering that is inconsistent with
         * equals.
         *
         * @param s1 the first subspace to compare
         * @param s2 the second subspace to compare
         * @return a negative integer, zero, or a positive integer if the coverage
         * of the first subspace is greater than, equal to, or less than the
         * coverage of the second subspace
         */
        @Override
        public int compare(CliqueSubspace<?> s1, CliqueSubspace<?> s2) {
            return -(s1.getCoverage() - s2.getCoverage());
        }
    }
}
