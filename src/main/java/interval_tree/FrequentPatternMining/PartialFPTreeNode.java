package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;
import interval_tree.SubspaceClustering.Clique;
import interval_tree.SubspaceClustering.MyRelation;
import interval_tree.SubspaceClustering.MyVector;

import java.util.List;

public class PartialFPTreeNode extends AbstractFPTree.FPTreeNode {

    private MyRelation<MyVector> relation;
    int dimensions;
    PartialFPTreeNode(PartialFPTreeNode parent, int dim) {
        super(parent);
        relation = new MyRelation<>(dim);
        this.dimensions = dim;
    }

    public void addData(MyVector vec){
        relation.insert(vec);
    }

    public List<IIndex> extractPartialIndexes(List<String> columns){
        return Clique.runClique(relation, columns.toArray(new String[0]));
    }

    @Override
    AbstractFPTree.FPTreeNode getOrCreateChild(String name){
        AbstractFPTree.FPTreeNode temp;
        if (children.containsKey(name)) {
            temp = children.get(name);
        } else {
            temp = new PartialFPTreeNode(this, dimensions + 1);

            children.put(name, temp);
        }
        temp.frequency++;
        return temp;
    }
}

