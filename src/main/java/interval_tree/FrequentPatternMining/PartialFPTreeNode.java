package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;
import interval_tree.Logger;
import interval_tree.SubspaceClustering.Clique;
import interval_tree.SubspaceClustering.MyVector;

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
    private PartialFPTreeNode(PartialFPTreeNode parent, String name) {
        super(parent, name);
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
    protected AbstractFPTreeNode clone(String column) {
        return new PartialFPTreeNode(this, column, dimensions + 1);
    }

    @Override
    protected AbstractFPTreeNode cloneRoot() {
        return new PartialFPTreeNode(null, "", 0);
    }

    @Override
    public List<IIndex> extractIndexes(String tableName, List<String> columns){
        return clique.getClusters(tableName, columns);
    }

    @Override
    public List<IIndex> extractIndexes(double frequency, String tableName, List<String> columns) {
        return null;
    }

    @Override
    void combineNode(AbstractFPTreeNode other) {

    }

//    @Override
//    protected PartialFPTreeNode clone() {
//        return new PartialFPTreeNode(this, dimensions + 1);
//    }
}

