package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.CompoundPartialIndex;
import interval_tree.CandidateIndex.FullIndex;
import interval_tree.CandidateIndex.IIndex;
import interval_tree.FrequentPatternMining.SupportCount.TableProperties;
import interval_tree.SubspaceClustering.Clique;
import interval_tree.SubspaceClustering.MyVector;

import java.util.LinkedList;
import java.util.List;

import static interval_tree.Experiment.IDEAL_COVERAGE;
import static interval_tree.Experiment.MINSUP;

public class PartialFPTreeNode extends AbstractFPTreeNode {

    private Clique<MyVector> clique;
    private int dimensions;
    PartialFPTreeNode(PartialFPTreeNode parent, String name, int dim) {
        super(parent, name);
        this.dimensions = dim;

        clique = new Clique<>(1000, MINSUP, IDEAL_COVERAGE, false, dim);
    }
    PartialFPTreeNode(PartialFPTreeNode parent, String name) {
        super(parent, name);
        this.dimensions = 0;

        clique = null;
    }

    private PartialFPTreeNode(String name) {
        super(null, name);
        this.dimensions = 0;
        clique = null;
    }

    void updateMinMax(MyVector data){
        clique.updateMinMax(data);
    }

    void initDimensions(){
        clique.initOneDimensionalUnits();
    }

    void addData(MyVector vec){
        clique.insertData(vec);
    }

    void findClusters(){
        clique.findClusters();
    }

    void validateClusters(MyVector vec){
        clique.validateClusters(vec);
    }

    @Override
    protected AbstractFPTreeNode makeChild(String column) {
        return new PartialFPTreeNode(this, column, dimensions + 1);
    }

    @Override
    public void extractIndexes(String tableName, List<String> columns, TableProperties tc){
        double[] negativeInfinity = new double[columns.size()];
        double[] positiveInfinity = new double[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            negativeInfinity[i] = tc.getNegativeInfinityLimit(tableName, column);
            positiveInfinity[i] = tc.getPositiveInfinityLimit(tableName, column);
        }
        indices = clique.getClusters(tableName, columns, negativeInfinity, positiveInfinity, tc);
    }

    @Override
    public List<IIndex> extractIndexes(double frequency, String tableName, List<String> columns) {

        List<IIndex> result = new LinkedList<>();
        result.add(new FullIndex(frequency, 0, tableName, String.join(",", columns)));
        for (IIndex index : indices) {
            CompoundPartialIndex tempComp = new CompoundPartialIndex(tableName);
            if(index instanceof CompoundPartialIndex){
                for (String treeNode : columns) {
                    tempComp.addCompoundPredicate(((CompoundPartialIndex) index).getPredicate(treeNode));
                }
                result.add(tempComp);
            }
        }
        return result;
    }

    @Override
    AbstractFPTreeNode clone(AbstractFPTreeNode other) {
        PartialFPTreeNode newNode = new PartialFPTreeNode(column);
        newNode.indices = other.indices;
        return newNode;
    }

}

