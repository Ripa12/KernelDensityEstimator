/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    UnivariateKernelEstimator.java
 *    Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package interval_tree.PDF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import interval_tree.DataStructure.IntervalTree;
import weka.core.RevisionUtils;
import weka.core.Statistics;
import weka.core.Utils;
import weka.estimators.UnivariateDensityEstimator;
import weka.estimators.UnivariateIntervalEstimator;
import weka.estimators.UnivariateQuantileEstimator;

/**
 * Simple weighted kernel density estimator.
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class UnivariateKernelEstimator implements UnivariateDensityEstimator,
        UnivariateIntervalEstimator, UnivariateQuantileEstimator, Serializable {


    /** For serialization */
    private static final long serialVersionUID = -1163983347810498880L;

    /** The collection used to store the weighted values. */
    protected TreeMap<Double, Double> m_TM = new TreeMap<Double, Double>();

    /** The weighted sum of values */
    protected double m_WeightedSum = 0;

    /** The weighted sum of squared values */
    protected double m_WeightedSumSquared = 0;

    /** The weight of the values collected so far */
    protected double m_SumOfWeights = 0;

    /** The current bandwidth (only computed when needed) */
    protected double m_Width = Double.MAX_VALUE;

    /** The exponent to use in computation of bandwidth (default: -0.25) */
    protected double m_Exponent = -0.25;

    /** The minimum allowed value of the kernel width (default: 1.0E-6) */
    protected double m_MinWidth = 1.0E-6;

    /** Constant for Gaussian density. */
    public static final double CONST = -0.5 * Math.log(2 * Math.PI);

    /** Threshold at which further kernels are no longer added to sum. */
    protected double m_Threshold = 1.0E-6;

    /** The number of intervals used to approximate prediction interval. */
    protected int m_NumIntervals = 1000;

    /**
     * Returns a string describing the estimator.
     */
    public String globalInfo() {
        return "Provides a univariate kernel estimator.";
    }
    /**
     * Adds a value to the density estimator.
     *
     * @param value the value to add
     * @param weight the weight of the value
     */
    @Override
    public void addValue(double value, double weight) {

        m_WeightedSum += value * weight;
        m_WeightedSumSquared += value * value * weight;
        m_SumOfWeights += weight;
        if (m_TM.get(value) == null) {
            m_TM.put(value, weight);
        } else {
            m_TM.put(value, m_TM.get(value) + weight);
        }
    }

    /**
     * Updates bandwidth: the sample standard deviation is multiplied by the total
     * weight to the power of the given exponent.
     *
     * If the total weight is not greater than zero, the width is set to
     * Double.MAX_VALUE. If that is not the case, but the width becomes smaller
     * than m_MinWidth, the width is set to the value of m_MinWidth.
     */
    public void updateWidth() {

        // OK, need to do some work
        if (m_SumOfWeights > 0) {

            // Compute variance for scaling
            double mean = m_WeightedSum / m_SumOfWeights;
            double variance = m_WeightedSumSquared / m_SumOfWeights - mean * mean;
            if (variance < 0) {
                variance = 0;
            }

            // Compute kernel bandwidth
            m_Width = Math.sqrt(variance) * Math.pow(m_SumOfWeights, m_Exponent);

            if (m_Width <= m_MinWidth) {
                m_Width = m_MinWidth;
            }
        } else {
            m_Width = Double.MAX_VALUE;
        }
    }

    /**
     * Returns the interval for the given confidence value.
     *
     * @param conf the confidence value in the interval [0, 1]
     * @return the interval
     */
    @Override
    public double[][] predictIntervals(double conf) {
        return new double[0][];
    }

//    @Override
//    public double[][] predictIntervals(double conf) {
    public double[][] predictIntervals(double conf, int oldMin, int oldMax) {
        // Update the bandwidth
        updateWidth();

        // Compute minimum and maximum value, and delta
        double val = Statistics.normalInverse(1.0 - (1.0 - conf) / 2);
        double min = m_TM.firstKey() - val * m_Width;
        double max = m_TM.lastKey() + val * m_Width;
        double delta = (max - min) / m_NumIntervals;

        // Create array with estimated probabilities
        double[] probabilities = new double[m_NumIntervals];
        double leftVal = Math.exp(logDensity(min));
        for (int i = 0; i < m_NumIntervals; i++) {
            double rightVal = Math.exp(logDensity(min + (i + 1) * delta));
            probabilities[i] = 0.5 * (leftVal + rightVal) * delta;
            leftVal = rightVal;
        }

        // Sort array based on area of bin estimates
        int[] sortedIndices = Utils.sort(probabilities);

        // Mark the intervals to use
        double sum = 0;
        boolean[] toUse = new boolean[probabilities.length];
        int k = 0;
        while ((sum < conf) && (k < toUse.length)) {
            toUse[sortedIndices[toUse.length - (k + 1)]] = true;
            sum += probabilities[sortedIndices[toUse.length - (k + 1)]];
            k++;
        }

        // Don't need probabilities anymore
        //probabilities = null;

        // Create final list of intervals
        ArrayList<double[]> intervals = new ArrayList<double[]>();

        // The current interval
        double[] interval = null;

        // Iterate through kernels
        boolean haveStartedInterval = false;
        for (int i = 0; i < m_NumIntervals; i++) {

            // Should the current bin be used?
            if (toUse[i]) {

                //double temp = probabilities[i];

                // Do we need to create a new interval?
                if (haveStartedInterval == false) {
                    haveStartedInterval = true;
                    interval = new double[3];
                    interval[0] = IntervalTree.rescale(min + i * delta, oldMin, oldMax);
                    interval[2] = 0;
                }

                // Regardless, we should update the upper boundary
                interval[1] = IntervalTree.rescale(min + (i + 1) * delta, oldMin, oldMax);
                interval[2] += probabilities[i];
            } else {

                // We need to finalize and store the last interval
                // if necessary.
                if (haveStartedInterval) {
                    haveStartedInterval = false;
                    intervals.add(interval);
                }
            }
        }

        // Add last interval if there is one
        if (haveStartedInterval) {
            intervals.add(interval);
        }

        return intervals.toArray(new double[0][0]);
    }

    /**
     * Returns the quantile for the given percentage.
     *
     * @param percentage the percentage
     * @return the quantile
     */
    @Override
    public double predictQuantile(double percentage) {

        // Update the bandwidth
        updateWidth();

        // Compute minimum and maximum value, and delta
        double val = Statistics.normalInverse(1.0 - (1.0 - 0.95) / 2);
        double min = m_TM.firstKey() - val * m_Width;
        double max = m_TM.lastKey() + val * m_Width;
        double delta = (max - min) / m_NumIntervals;

        double sum = 0;
        double leftVal = Math.exp(logDensity(min));
        for (int i = 0; i < m_NumIntervals; i++) {
            if (sum >= percentage) {
                return min + i * delta;
            }
            double rightVal = Math.exp(logDensity(min + (i + 1) * delta));
            sum += 0.5 * (leftVal + rightVal) * delta;
            leftVal = rightVal;
        }
        return max;
    }

    /**
     * Computes the logarithm of x and y given the logarithms of x and y.
     *
     * This is based on Tobias P. Mann's description in "Numerically Stable Hidden
     * Markov Implementation" (2006).
     */
    protected double logOfSum(double logOfX, double logOfY) {

        // Check for cases where log of zero is present
        if (Double.isNaN(logOfX)) {
            return logOfY;
        }
        if (Double.isNaN(logOfY)) {
            return logOfX;
        }

        // Otherwise return proper result, taken care of overflows
        if (logOfX > logOfY) {
            return logOfX + Math.log(1 + Math.exp(logOfY - logOfX));
        } else {
            return logOfY + Math.log(1 + Math.exp(logOfX - logOfY));
        }
    }

    /**
     * Compute running sum of density values and weights.
     */
    protected void runningSum(Set<Map.Entry<Double, Double>> c, double value,
                              double[] sums) {

        // Auxiliary variables
        double offset = CONST - Math.log(m_Width);
        double logFactor = Math.log(m_Threshold) - Math.log(1 - m_Threshold);
        double logSumOfWeights = Math.log(m_SumOfWeights);

        // Iterate through values
        Iterator<Map.Entry<Double, Double>> itr = c.iterator();
        while (itr.hasNext()) {
            Map.Entry<Double, Double> entry = itr.next();

            // Skip entry if weight is zero because it cannot contribute to sum
            if (entry.getValue() > 0) {


                double diff = (entry.getKey() - value) / m_Width;
                double logDensity = offset - 0.5 * diff * diff;
                double logWeight = Math.log(entry.getValue());
                sums[0] = logOfSum(sums[0], logWeight + logDensity);
                sums[1] = logOfSum(sums[1], logWeight);

                // Can we stop assuming worst case?
                if (logDensity + logSumOfWeights < logOfSum(logFactor + sums[0],
                        logDensity + sums[1])) {
                    break;
                }
            }
        }
    }

    /**
     * Returns the natural logarithm of the density estimate at the given point.
     *
     * @param value the value at which to evaluate
     * @return the natural logarithm of the density estimate at the given value
     */
    @Override
    public double logDensity(double value) {

        // Update the bandwidth
        updateWidth();

        // Array used to keep running sums
        double[] sums = new double[2];
        sums[0] = Double.NaN;
        sums[1] = Double.NaN;

        // Examine right-hand size of value
        runningSum(m_TM.tailMap(value, true).entrySet(), value, sums);

        // Examine left-hand size of value
        runningSum(m_TM.headMap(value, false).descendingMap().entrySet(), value,
                sums);

        // Need to normalize
        return sums[0] - Math.log(m_SumOfWeights);
    }

    /**
     * Returns textual description of this estimator.
     */
    @Override
    public String toString() {

        return "Kernel estimator with bandwidth " + m_Width + " and total weight "
                + m_SumOfWeights + " based on\n" + m_TM.toString();
    }

    /**
     * Returns the revision string.
     *
     * @return the revision
     */
    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision$");
    }
}