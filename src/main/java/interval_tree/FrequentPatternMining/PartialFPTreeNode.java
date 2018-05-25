package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;
import interval_tree.Logger;
import interval_tree.SubspaceClustering.Clique;
import interval_tree.SubspaceClustering.MyVector;

import java.util.List;

import static interval_tree.Experiment.MINSUP;

public class PartialFPTreeNode extends AbstractFPTreeNode {

    private Clique<MyVector> clique;
    private int dimensions;
    PartialFPTreeNode(PartialFPTreeNode parent, int dim) {
        super(parent);
        this.dimensions = dim;

        clique = new Clique<>(1000, MINSUP, .7, false, dim);
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
    public List<IIndex> extractIndexes(String tableName, List<String> columns){
        return clique.getClusters(tableName, columns);
    }

    @Override
    protected PartialFPTreeNode clone() {
        return new PartialFPTreeNode(this, dimensions + 1);
    }
}

