package interval_tree.SubspaceClustering;

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.CLIQUE;
import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.clique.CLIQUEInterval;
import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.clique.CLIQUESubspace;
import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.clique.CLIQUEUnit;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.model.SubspaceModel;
import interval_tree.CandidateIndex.CompoundPartialIndex;
import interval_tree.CandidateIndex.FullIndex;
import interval_tree.CandidateIndex.IIndex;
import interval_tree.CandidateIndex.PartialIndex;

import java.util.*;

public class Clique {

    private final static double MIN_COVERAGE = .7;

    public static List<IIndex> runClique(MyRelation<MyVector> rel, String[] columns, int totalCoverage) {
        List<IIndex> candidates = new LinkedList<>();

        // run CLIQUE on database
        // ToDo: i should be dependent on the max and min of the data (more units if range is wider)
        Clustering<SubspaceModel> result = new CLIQUE<MyVector>(1000, .1, true).run(rel); // ToDo: 50% support count?

        int denseCoverage = 0;
//        List<Integer> sizes = new java.util.ArrayList<>();
        for (Cluster<SubspaceModel> cl : result.getAllClusters()) {
//            sizes.add(cl.size());

            if(cl.getModel().getSubspace() instanceof CLIQUESubspace) {
                CLIQUESubspace cliqueSubspace = (CLIQUESubspace) cl.getModel().getSubspace(); // ToDo: check maximum dimensionality here to avoid redundant computation!
                if(cliqueSubspace.dimensionality() == columns.length){
                    System.out.print(" -- Subspace -- \n");
                    List<CLIQUEUnit> units = cliqueSubspace.getDenseUnits();
                    System.out.println("TotalCoverage: " + totalCoverage);
                    System.out.println("Coverage: " + cliqueSubspace.getCoverage());

                    System.out.println("Dimension: " + cliqueSubspace.dimensonsToString());
                    System.out.println("Dimension: " + cliqueSubspace.dimensionality());

                    denseCoverage += cliqueSubspace.getCoverage();
                    double dimensions[][] = new double[columns.length][3];
                    for (double[] row: dimensions){
                        row[0] = Double.MAX_VALUE;
                        row[1] = Double.MIN_VALUE;
                    }

                    for (CLIQUEUnit unit : units) {

                        System.out.print("\t -- Unit -- \n");
                        System.out.println("\tFeature vectors: " + unit.numberOfFeatureVectors());
                        System.out.println("\tSelectivity: " + unit.selectivity(unit.numberOfFeatureVectors()));
                        ArrayList<CLIQUEInterval> intervals = unit.getIntervals();

                        int k = 0;
                        for (CLIQUEInterval interval : intervals) {
                            System.out.print("\t\t -- Interval -- \n");
                            System.out.print("\t\tDimension: " + interval.getDimension() + ", ");
                            System.out.print("\t\tMax: " + interval.getMax() + ", ");
                            System.out.println("\t\tMin: " + interval.getMin());

                            dimensions[k][0] = Math.min(dimensions[k][0], interval.getMin());
                            dimensions[k][1] = Math.max(dimensions[k][1], interval.getMax());
                            dimensions[k][2] = interval.getDimension();
                            k++;
                        }
                    }
                    for (double[] dimension : dimensions) {
                        System.out.print("\t\t -- Combined Interval -- \n");
                        System.out.print("\t\t\t Min: " + dimension[0]);
                        System.out.println("\t\t\t Max: " + dimension[1]);
                    }

//                if(dimensions.length > 1){ // ToDo: Should probably only mine for itemsets at maximum dimension!



                    CompoundPartialIndex compoundPartialIndex = new CompoundPartialIndex();

                    for(int k = 0; k < dimensions.length; k++){

                        // ToDo: only add if coverage > minsup
                        PartialIndex pIndex = new PartialIndex(((double) cliqueSubspace.getCoverage()), 0, columns[(int)dimensions[k][2]], (int)dimensions[k][0], (int)dimensions[k][1]);
                        CompoundPartialIndex.Predicate pred = new CompoundPartialIndex.Predicate();
                        pred.addPartialIndex(pIndex);
                        compoundPartialIndex.addCompoundPredicate(pred);
                    }
                    candidates.add(compoundPartialIndex);
                }
//                else{ // ToDo: Should probably only mine for itemsets at maximum dimension!
//                    candidates.add(new PartialIndex(((double) cliqueSubspace.getCoverage()), 0, columns[(int)(int)dimensions[0][2]], (int)dimensions[0][0], (int)dimensions[0][1]));
//                }

            }

        }

        // Debugging
        if(candidates.size() == 0){
            System.out.println("No Candidates found");
        }

        if(((double)denseCoverage)/((double)totalCoverage) < MIN_COVERAGE){
            candidates.clear();
            candidates.add(new FullIndex(totalCoverage, 0, String.join(",", columns)));
        }

        return candidates;
    }
}
