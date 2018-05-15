package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;
import interval_tree.SubspaceClustering.Clique;
import interval_tree.SubspaceClustering.MyData;
import interval_tree.SubspaceClustering.MyRelation;
import interval_tree.SubspaceClustering.MyVector;

import java.util.List;

public class PartialFPTreeNode extends AbstractFPTree.FPTreeNode {

    Clique<MyVector> clique;
    int dimensions;
    PartialFPTreeNode(PartialFPTreeNode parent, int dim) {
        super(parent);
        this.dimensions = dim;

        clique = new Clique<>(1000, .1, false, dim);
    }

    public void updateMinMax(MyVector data){
        clique.updateMinMax(data);
    }

    public void initDimensions(){
        clique.initOneDimensionalUnits();
    }

    public void addData(MyVector vec){
        clique.insertData(vec);
    }

    public void findClusters(){
        clique.findClusters();
    }

    public void validateClusters(MyVector vec){
        clique.validateClusters(vec);
    }

    public List<IIndex> extractPartialIndexes(List<String> columns){
//        return Clique.runClique(relation, columns.toArray(new String[0]), frequency);
        return clique.getClusters(columns);

    }

    @Override
    protected AbstractFPTree.FPTreeNode clone() {
        return new PartialFPTreeNode(this, dimensions + 1);
    }
}

