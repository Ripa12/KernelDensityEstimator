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

import java.util.*;

import de.lmu.ifi.dbs.elki.algorithm.AbstractAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.CLIQUE;
import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.SubspaceClusteringAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.clique.CLIQUEInterval;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.Subspace;
import de.lmu.ifi.dbs.elki.data.model.SubspaceModel;
import de.lmu.ifi.dbs.elki.data.type.TypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.CommonConstraints;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Flag;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;
import interval_tree.CandidateIndex.CompoundPartialIndex;
import interval_tree.FrequentPatternMining.SupportCount.TableProperties;

/**
 * <p/>
 * Implementation of the CLIQUE algorithm, a grid-based algorithm to identify
 * dense clusters in subspaces of maximum dimensionality.
 * </p>
 * <p/>
 * The implementation consists of two steps: <br>
 * 1. Identification of subspaces that contain clusters <br>
 * 2. Identification of clusters
 * </p>
 * <p/>
 * The third step of the original algorithm (Generation of minimal description
 * for the clusters) is not (yet) implemented.
 * </p>
 * <p>
 * Reference: <br>
 * R. Agrawal, J. Gehrke, D. Gunopulos, P. Raghavan: Automatic Subspace
 * Clustering of High Dimensional Data for Data Mining Applications. <br>
 * In Proc. ACM SIGMOD Int. Conf. on Management of Data, Seattle, WA, 1998.
 * </p>
 *
 * @param <V> the type of NumberVector handled by this Algorithm
 * @author Elke Achtert
 * @apiviz.has SubspaceModel
 * @apiviz.has CliqueSubspace
 * @apiviz.uses CliqueUnit
 * @since 0.2
 */
@Title("CLIQUE: Automatic Subspace Clustering of High Dimensional Data for Data Mining Applications")
@Description("Grid-based algorithm to identify dense clusters in subspaces of maximum dimensionality.")
@Reference(authors = "R. Agrawal, J. Gehrke, D. Gunopulos, P. Raghavan", title = "Automatic Subspace Clustering of High Dimensional Data for Data Mining Applications", booktitle = "Proc. SIGMOD Conference, Seattle, WA, 1998", url = "http://dx.doi.org/10.1145/276304.276314")
public class Clique<V extends MyVector> extends AbstractAlgorithm<Clustering<SubspaceModel>> implements SubspaceClusteringAlgorithm<SubspaceModel> {
    /**
     * The logger for this class.
     */
    private static final Logging LOG = Logging.getLogger(CLIQUE.class);

    /**
     * Parameter to specify the number of intervals (units) in each dimension,
     * must be an integer greater than 0.
     * <p>
     * Key: {@code -clique.xsi}
     * </p>
     */
    public static final OptionID XSI_ID = new OptionID("clique.xsi", "The number of intervals (units) in each dimension.");

    /**
     * Parameter to specify the density threshold for the selectivity of a unit,
     * where the selectivity is the fraction of total feature vectors contained in
     * this unit, must be a double greater than 0 and less than 1.
     * <p>
     * Key: {@code -clique.tau}
     * </p>
     */
    public static final OptionID TAU_ID = new OptionID("clique.tau", "The density threshold for the selectivity of a unit, where the selectivity is" + "the fraction of total feature vectors contained in this unit.");

    /**
     * Flag to indicate that only subspaces with large coverage (i.e. the fraction
     * of the database that is covered by the dense units) are selected, the rest
     * will be pruned.
     * <p>
     * Key: {@code -clique.prune}
     * </p>
     */
    public static final OptionID PRUNE_ID = new OptionID("clique.prune", "Flag to indicate that only subspaces with large coverage " + "(i.e. the fraction of the database that is covered by the dense units) " + "are selected, the rest will be pruned.");

    /**
     * Holds the value of {@link #XSI_ID}.
     */
    private int xsi;

    /**
     * Holds the value of {@link #TAU_ID}.
     */
    private double tau;

    /**
     * Holds the value of {@link #PRUNE_ID}.
     */
    private boolean prune;

    /**
     * Holds the value of {}.
     */
    private double idealCoverage;


    /**
     * My Variables.
     */
    private double[] minima;
    private double[] maxima;
    private int dimensionality;

    int total;

    ArrayList<CliqueUnit<V>> units;
    List<Pair<Subspace, ModifiableDBIDs>> modelsAndClusters;


    /**
     * Constructor.
     *
     * @param xsi   Xsi value
     * @param tau   Tau value
     * @param prune Prune flag
     */
    public Clique(int xsi, double tau, double idealCoverage, boolean prune, int dimensionality) {
        super();
        this.xsi = xsi;
        this.tau = tau;
        this.idealCoverage = idealCoverage;
        this.prune = prune;

        this.total = 0;

        this.dimensionality = dimensionality;

        // initialize minima and maxima
        minima = new double[dimensionality];
        maxima = new double[dimensionality];
        for (int d = 0; d < dimensionality; d++) {
            maxima[d] = -Double.MAX_VALUE;
            minima[d] = Double.MAX_VALUE;
        }
    }

    /**
     * Updates the minima and maxima array according to the specified feature
     * vector.
     *
     * @param featureVector the feature vector
    //     * @param minima        the array of minima
    //     * @param maxima        the array of maxima
     */
    public void updateMinMax(V featureVector) {
        if (minima.length != featureVector.getDimensionality()) {
            throw new IllegalArgumentException("FeatureVectors differ in length.");
        }
        for (int d = 0; d < featureVector.getDimensionality(); d++) {
            if ((featureVector.getMax(d)) > maxima[d]) {
                maxima[d] = (featureVector.getMax(d));
            }
            if ((featureVector.getMin(d)) < minima[d]) {
                minima[d] = (featureVector.getMin(d));
            }
        }
    }

    /**
     * Initializes and returns the one dimensional units.
     *
     //     * @param database the database to run the algorithm on
     * @return the created one dimensional units
     */
    public void initOneDimensionalUnits() {

        for (int i = 0; i < maxima.length; i++) {
            maxima[i] += 0.0001; // ToDo: This is the reason why infinity did not work
        }

        // determine the unit length in each dimension
        double[] unit_lengths = new double[dimensionality];
        for (int d = 0; d < dimensionality; d++) {
            unit_lengths[d] = (maxima[d] - minima[d]) / xsi;
        }

        // determine the boundaries of the units
        double[][] unit_bounds = new double[xsi + 2][dimensionality];
        for (int x = 1; x <= (xsi); x++) {
            for (int d = 0; d < dimensionality; d++) {
                if (x < xsi) {
                    unit_bounds[x][d] = minima[d] + (x - 1) * unit_lengths[d];
                } else {
                    unit_bounds[x][d] = maxima[d];
                }
            }
        }
        for (int d = 0; d < dimensionality; d++) {
            unit_bounds[0][d] = Double.NEGATIVE_INFINITY;
            unit_bounds[xsi+1][d] = Double.POSITIVE_INFINITY;
        }

        // build the 1 dimensional units
        units = new ArrayList<>(((xsi + 1) * dimensionality));
        for (int d = 0; d < dimensionality; d++) {
            for (int x = 0; x <= xsi; x++) {
//            for (int d = 0; d < dimensionality; d++) {
                units.add(new CliqueUnit<>(new CLIQUEInterval(d, unit_bounds[x][d], unit_bounds[x + 1][d])));
            }
        }
        return;
    }

    public void insertData(V featureVector){
        for (int i = 0; i < featureVector.getDimensionality(); i++) {
            binaryInsert(units, i * (xsi + 1), ((i + 1) * (xsi + 1)) - 1, featureVector);
        }

        total++; // ToDo: Only increment if featureVector is inserted successively!
    }

    /**
     * Performs the CLIQUE algorithm on the given database.
     *
     //     * @param relation Data relation to process
     * @return Clustering result
     */
    public void findClusters() {
        // 1. Identification of subspaces that contain clusters
        SortedMap<Integer, List<CliqueSubspace<V>>> dimensionToDenseSubspaces = new TreeMap<>();
        List<CliqueSubspace<V>> denseSubspaces = findOneDimensionalDenseSubspaces();
        dimensionToDenseSubspaces.put(Integer.valueOf(0), denseSubspaces);


        long startTime = System.nanoTime();
        for (int k = 2; k <= dimensionality && !denseSubspaces.isEmpty(); k++) {
            denseSubspaces = findDenseSubspaces(denseSubspaces);
            dimensionToDenseSubspaces.put(Integer.valueOf(k - 1), denseSubspaces);
            if (LOG.isVerbose()) {
                LOG.verbose("    " + k + "-dimensional dense subspaces: " + denseSubspaces.size());
            }
            if (LOG.isDebugging()) {
                for (CliqueSubspace<V> s : denseSubspaces) {
                    LOG.debug(s.toString("      "));
                }
            }
        }

        Integer dim = dimensionToDenseSubspaces.lastKey();
        List<CliqueSubspace<V>> subspaces = dimensionToDenseSubspaces.get(dim);
        modelsAndClusters = determineClusters(subspaces);

        // ToDo: Would be better to return clusters with 0 coverage, rather than loopoing over them here!
        for (Pair<Subspace, ModifiableDBIDs> modelAndCluster : modelsAndClusters) {
            ((CliqueSubspace) modelAndCluster.getFirst()).resetCoverage();
        }
    }

    public void validateClusters(V featureVector){
        for (Pair<Subspace, ModifiableDBIDs> modelAndCluster : modelsAndClusters) {
            ((CliqueSubspace) modelAndCluster.getFirst()).contains(featureVector);
        }
    }

    public List<CompoundPartialIndex> getClusters(String tableName, List<String> columns,
                                    double[] negativeInfinity, double[] positiveInfinity, TableProperties tc){
        if(modelsAndClusters == null)
            return null;

        List<CompoundPartialIndex> candidates = new LinkedList<>();
        double accumulatedCoverage = 0;

        for (Pair<Subspace, ModifiableDBIDs> modelAndCluster : modelsAndClusters) {

            double coverage = ((CliqueSubspace) modelAndCluster.getFirst()).getCoverage();

            // ToDo: Are too many candidate clusters created before being pruned?
            if ((coverage / ((double) total)) >= tau) {
                candidates.add(((CliqueSubspace) modelAndCluster.getFirst()).makePartialIndex(tableName, columns,
                        negativeInfinity, positiveInfinity, tc));
                accumulatedCoverage += coverage;
            }
        }

        // ToDo: Macro here
        if((accumulatedCoverage / ((double) total)) < idealCoverage){
            candidates.clear();
        }

        return candidates;
    }

    /**
     * Determines the clusters in the specified dense subspaces.
     *
     * @param denseSubspaces the dense subspaces in reverse order by their
     *                       coverage
     * @return the clusters in the specified dense subspaces and the corresponding
     * cluster models
     */
    private List<Pair<Subspace, ModifiableDBIDs>> determineClusters(List<CliqueSubspace<V>> denseSubspaces) {
        List<Pair<Subspace, ModifiableDBIDs>> clusters = new ArrayList<>();

        for (CliqueSubspace<V> subspace : denseSubspaces) {
            List<Pair<Subspace, ModifiableDBIDs>> clustersInSubspace = subspace.determineClusters();
            if (LOG.isDebugging()) {
                LOG.debugFine("Subspace " + subspace + " clusters " + clustersInSubspace.size());
            }
            clusters.addAll(clustersInSubspace);
        }
        return clusters;
    }

    /**
     * Determines the one dimensional dense subspaces and performs a pruning if
     * this option is chosen.
     *
     //     * @param database the database to run the algorithm on
     * @return the one dimensional dense subspaces reverse ordered by their
     * coverage
     */
    private List<CliqueSubspace<V>> findOneDimensionalDenseSubspaces() {
        List<CliqueSubspace<V>> denseSubspaceCandidates = findOneDimensionalDenseSubspaceCandidates();

        if (prune) {
            return pruneDenseSubspaces(denseSubspaceCandidates);
        }

        return denseSubspaceCandidates;
    }

    /**
     * Determines the {@code k}-dimensional dense subspaces and performs a pruning
     * if this option is chosen.
     *
     //     * @param database       the database to run the algorithm on
     * @param denseSubspaces the {@code (k-1)}-dimensional dense subspaces
     * @return a list of the {@code k}-dimensional dense subspaces sorted in
     * reverse order by their coverage
     */
    private List<CliqueSubspace<V>> findDenseSubspaces(List<CliqueSubspace<V>> denseSubspaces) {
        List<CliqueSubspace<V>> denseSubspaceCandidates = findDenseSubspaceCandidates(denseSubspaces);

        if (prune) {
            return pruneDenseSubspaces(denseSubspaceCandidates);
        }

        return denseSubspaceCandidates;
    }



    // Code from GeeksForGeeks
    private void binaryInsert(ArrayList<CliqueUnit<V>> units, int l, int u, V vector) {
        assert u >= l;

        int j = 0;
        int lower = l;
        int upper = u;
        int curIn = 0;

        double value = 0;
        CLIQUEInterval a = null;
        while (lower <= upper) {

            curIn = (lower + upper) / 2;

            a = units.get(curIn).getIntervals().get(0);
            value = vector.getMin(a.getDimension());

            if (a.getMin() < value)
                lower = curIn + 1;
            else if (a.getMin() > value)
                upper = curIn - 1;
            else if (a.getMin() == value)
                break;
        }
        j = curIn;

        units.get(j).addFeatureVector(null, vector);

        boolean terminate = false;
        while (!terminate) {
            j++;
            if (j > u || !units.get(j).addFeatureVector(null, vector)) {
                terminate = true;
            }
        }
    }

    /**
     * Determines the one-dimensional dense subspace candidates by making a pass
     * over the database.
     *
     //     * @param database the database to run the algorithm on
     * @return the one-dimensional dense subspace candidates reverse ordered by
     * their coverage
     */
    private List<CliqueSubspace<V>> findOneDimensionalDenseSubspaceCandidates() {
        if(units == null){
            return null;
        }

        ArrayList<CliqueUnit<V>> denseUnits = new ArrayList<>();
        Map<Integer, CliqueSubspace<V>> denseSubspaces = new HashMap<>();
        for (CliqueUnit<V> unit : units) {
            // unit is a dense unit
            if (unit.selectivity(total) >= tau) {
                denseUnits.add(unit);
                // add the dense unit to its subspace
                int dim = unit.getIntervals().iterator().next().getDimension();
                CliqueSubspace<V> subspace_d = denseSubspaces.get(Integer.valueOf(dim));
                if (subspace_d == null) {
                    subspace_d = new CliqueSubspace<>(dim);
                    denseSubspaces.put(Integer.valueOf(dim), subspace_d);
                }

                subspace_d.addDenseUnit(unit);
            }
        }

        // Prune // ToDo: Make this code nicer
        for (CliqueSubspace<V> denseSubspace : denseSubspaces.values()) {
            ArrayList<CliqueUnit<V>> prunedDenseUnits = new ArrayList<>();
            int i_ = 0;
            for (int i = 0; i < denseSubspace.getDenseUnits().size() - 1; i++) {
                if (!denseSubspace.getDenseUnits().get(i).containsLeftNeighbor(denseSubspace.getDenseUnits().get(i + 1).getIntervals().get(0))) {
                    if (i != i_) {
                        CLIQUEInterval firstInterval = denseSubspace.getDenseUnits().get(i_).getIntervals().get(0);
                        CLIQUEInterval secondInterval = denseSubspace.getDenseUnits().get(i).getIntervals().get(0);

                        prunedDenseUnits.add(new CliqueUnit<>(new CLIQUEInterval(firstInterval.getDimension(),
                                firstInterval.getMin(),
                                secondInterval.getMax())));
                    } else {
                        prunedDenseUnits.add(denseSubspace.getDenseUnits().get(i));
                    }
                    i_ = i + 1;
                }
            }
            if (i_ <= denseSubspace.getDenseUnits().size() - 1) {
                CLIQUEInterval firstInterval = denseSubspace.getDenseUnits().get(i_).getIntervals().get(0);
                CLIQUEInterval secondInterval = denseSubspace.getDenseUnits().get(denseSubspace.getDenseUnits().size() - 1).getIntervals().get(0);

                prunedDenseUnits.add(new CliqueUnit<>(new CLIQUEInterval(firstInterval.getDimension(),
                        firstInterval.getMin(),
                        secondInterval.getMax())));
            } else {
                if (!denseSubspace.getDenseUnits().get(denseSubspace.getDenseUnits().size() - 2).containsRightNeighbor(denseSubspace.getDenseUnits().get(denseSubspace.getDenseUnits().size() - 1).getIntervals().get(0))) {
                    CLIQUEInterval firstInterval = denseSubspace.getDenseUnits().get(denseSubspace.getDenseUnits().size() - 2).getIntervals().get(0);
                    CLIQUEInterval secondInterval = denseSubspace.getDenseUnits().get(denseSubspace.getDenseUnits().size() - 1).getIntervals().get(0);
                    prunedDenseUnits.remove(prunedDenseUnits.size() - 1);

                    prunedDenseUnits.add(new CliqueUnit<>(new CLIQUEInterval(firstInterval.getDimension(),
                            firstInterval.getMin(),
                            secondInterval.getMax())));
                } else {
                    prunedDenseUnits.add(denseSubspace.getDenseUnits().get(denseSubspace.getDenseUnits().size() - 1));
                }
            }
            denseSubspace.setDenseUnits(prunedDenseUnits);
        }

        List<CliqueSubspace<V>> subspaceCandidates = new ArrayList<>(denseSubspaces.values());
        Collections.sort(subspaceCandidates, new CliqueSubspace.CoverageComparator());
        return subspaceCandidates;
    }

    /**
     * Determines the {@code k}-dimensional dense subspace candidates from the
     * specified {@code (k-1)}-dimensional dense subspaces.
     *
     //     * @param database       the database to run the algorithm on
     * @param denseSubspaces the {@code (k-1)}-dimensional dense subspaces
     * @return a list of the {@code k}-dimensional dense subspace candidates
     * reverse ordered by their coverage
     */
    private List<CliqueSubspace<V>> findDenseSubspaceCandidates(List<CliqueSubspace<V>> denseSubspaces) {
        // sort (k-1)-dimensional dense subspace according to their dimensions
        List<CliqueSubspace<V>> denseSubspacesByDimensions = new ArrayList<>(denseSubspaces);
        Collections.sort(denseSubspacesByDimensions, new Subspace.DimensionComparator());

        // determine k-dimensional dense subspace candidates
        double all = total;
        List<CliqueSubspace<V>> denseSubspaceCandidates = new ArrayList<>();

        while (!denseSubspacesByDimensions.isEmpty()) {
            CliqueSubspace<V> s1 = denseSubspacesByDimensions.remove(0);
            for (CliqueSubspace<V> s2 : denseSubspacesByDimensions) {
                CliqueSubspace<V> s = s1.join(s2, all, tau);
                if (s != null) {
                    denseSubspaceCandidates.add(s);
                }
            }
        }

        // sort reverse by coverage
        Collections.sort(denseSubspaceCandidates, new CliqueSubspace.CoverageComparator());
        return denseSubspaceCandidates;
    }

    /**
     * Performs a MDL-based pruning of the specified dense subspaces as described
     * in the CLIQUE algorithm.
     *
     * @param denseSubspaces the subspaces to be pruned sorted in reverse order by
     *                       their coverage
     * @return the subspaces which are not pruned reverse ordered by their
     * coverage
     */
    private List<CliqueSubspace<V>> pruneDenseSubspaces(List<CliqueSubspace<V>> denseSubspaces) {
        int[][] means = computeMeans(denseSubspaces);
        double[][] diffs = computeDiffs(denseSubspaces, means[0], means[1]);
        double[] codeLength = new double[denseSubspaces.size()];
        double minCL = Double.MAX_VALUE;
        int min_i = -1;

        for (int i = 0; i < denseSubspaces.size(); i++) {
            int mi = means[0][i];
            int mp = means[1][i];
            double log_mi = mi == 0 ? 0 : StrictMath.log(mi) / StrictMath.log(2);
            double log_mp = mp == 0 ? 0 : StrictMath.log(mp) / StrictMath.log(2);
            double diff_mi = diffs[0][i];
            double diff_mp = diffs[1][i];
            codeLength[i] = log_mi + diff_mi + log_mp + diff_mp;

            if (codeLength[i] <= minCL) {
                minCL = codeLength[i];
                min_i = i;
            }
        }

        return denseSubspaces.subList(0, min_i + 1);
    }

    /**
     * The specified sorted list of dense subspaces is divided into the selected
     * set I and the pruned set P. For each set the mean of the cover fractions is
     * computed.
     *
     * @param denseSubspaces the dense subspaces in reverse order by their
     *                       coverage
     * @return the mean of the cover fractions, the first value is the mean of the
     * selected set I, the second value is the mean of the pruned set P.
     */
    private int[][] computeMeans(List<CliqueSubspace<V>> denseSubspaces) {
        int n = denseSubspaces.size() - 1;

        int[] mi = new int[n + 1];
        int[] mp = new int[n + 1];

        double resultMI = 0;
        double resultMP = 0;

        for (int i = 0; i < denseSubspaces.size(); i++) {
            resultMI += denseSubspaces.get(i).getCoverage();
            resultMP += denseSubspaces.get(n - i).getCoverage();
            mi[i] = (int) Math.ceil(resultMI / (i + 1));
            if (i != n) {
                mp[n - 1 - i] = (int) Math.ceil(resultMP / (i + 1));
            }
        }

        int[][] result = new int[2][];
        result[0] = mi;
        result[1] = mp;

        return result;
    }

    /**
     * The specified sorted list of dense subspaces is divided into the selected
     * set I and the pruned set P. For each set the difference from the specified
     * mean values is computed.
     *
     * @param denseSubspaces denseSubspaces the dense subspaces in reverse order
     *                       by their coverage
     * @param mi             the mean of the selected sets I
     * @param mp             the mean of the pruned sets P
     * @return the difference from the specified mean values, the first value is
     * the difference from the mean of the selected set I, the second
     * value is the difference from the mean of the pruned set P.
     */
    private double[][] computeDiffs(List<CliqueSubspace<V>> denseSubspaces, int[] mi, int[] mp) {
        int n = denseSubspaces.size() - 1;

        double[] diff_mi = new double[n + 1];
        double[] diff_mp = new double[n + 1];

        double resultMI = 0;
        double resultMP = 0;

        for (int i = 0; i < denseSubspaces.size(); i++) {
            double diffMI = Math.abs(denseSubspaces.get(i).getCoverage() - mi[i]);
            resultMI += diffMI == 0.0 ? 0 : StrictMath.log(diffMI) / StrictMath.log(2);
            double diffMP = (i != n) ? Math.abs(denseSubspaces.get(n - i).getCoverage() - mp[n - 1 - i]) : 0;
            resultMP += diffMP == 0.0 ? 0 : StrictMath.log(diffMP) / StrictMath.log(2);
            diff_mi[i] = resultMI;
            if (i != n) {
                diff_mp[n - 1 - i] = resultMP;
            }
        }
        double[][] result = new double[2][];
        result[0] = diff_mi;
        result[1] = diff_mp;

        return result;
    }

    @Override
    public TypeInformation[] getInputTypeRestriction() {
        return TypeUtil.array(TypeUtil.NUMBER_VECTOR_FIELD);
    }

    @Override
    protected Logging getLogger() {
        return LOG;
    }

    /**
     * Parameterization class.
     *
     * @author Erich Schubert
     * @apiviz.exclude
     */
    public static class Parameterizer<V extends NumberVector> extends AbstractParameterizer {
        protected int xsi;

        protected double tau;

        protected boolean prune;

        @Override
        protected void makeOptions(Parameterization config) {
            super.makeOptions(config);
            IntParameter xsiP = new IntParameter(XSI_ID);
            xsiP.addConstraint(CommonConstraints.GREATER_EQUAL_ONE_INT);
            if (config.grab(xsiP)) {
                xsi = xsiP.intValue();
            }

            DoubleParameter tauP = new DoubleParameter(TAU_ID);
            tauP.addConstraint(CommonConstraints.GREATER_THAN_ZERO_DOUBLE);
            tauP.addConstraint(CommonConstraints.LESS_THAN_ONE_DOUBLE);
            if (config.grab(tauP)) {
                tau = tauP.doubleValue();
            }

            Flag pruneF = new Flag(PRUNE_ID);
            if (config.grab(pruneF)) {
                prune = pruneF.isTrue();
            }
        }

        @Override
        protected CLIQUE<V> makeInstance() {
            return new CLIQUE<>(xsi, tau, prune);
        }
    }
}