package Indexer.FrequentPatternMining.Partial;

import Indexer.CandidateIndex.CompoundPartialIndex;
import Indexer.CandidateIndex.FullIndex;
import Indexer.CandidateIndex.IIndex;
import Indexer.FrequentPatternMining.AbstractFPTreeNode;
import Indexer.SubspaceClustering.Clique;
import Indexer.SubspaceClustering.MyVector;

import java.util.LinkedList;
import java.util.List;


public class PartialFPTreeNode extends AbstractFPTreeNode {

    private Clique<MyVector> clique;
    private int dimensions;
    PartialFPTreeNode(PartialFPTreeNode parent, String name, int dim, int nrOfCells, double clusterThreshold) {
        super(parent, name);
        this.dimensions = dim;

        clique = new Clique<>(nrOfCells,
                clusterThreshold,
                0,
                false, dim);
    }
    PartialFPTreeNode(PartialFPTreeNode parent, String name, int dim, Clique clique) {
        super(parent, name);
        this.dimensions = dim;

        this.clique = clique;
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

    Clique<MyVector> getClique(){
        return clique;
    }

    @Override
    protected AbstractFPTreeNode makeChild(String column) {
        return new PartialFPTreeNode(this, column, dimensions + 1, new Clique(this.clique, dimensions + 1));
    }

    void extractIndexes(String tableName, List<String> columns){
        setIndices(clique.getClusters(tableName, columns));
    }

    @Override
    public List<IIndex> extractIndexes(double frequency, String tableName, List<String> columns) {

        List<IIndex> result = new LinkedList<>();
        result.add(new FullIndex(frequency, 0, tableName, String.join(",", columns)));
        for (IIndex index : getIndices()) {
            CompoundPartialIndex tempComp = new CompoundPartialIndex(tableName);
            if(index instanceof CompoundPartialIndex){
                for (String treeNode : columns) {
                    tempComp.addCompoundPredicate(((CompoundPartialIndex) index).getPredicateClone(treeNode));
                }
                result.add(tempComp);
            }
        }
        return result;
    }

    @Override
    protected AbstractFPTreeNode doClone(AbstractFPTreeNode other, List<? extends IIndex> indices) {
        PartialFPTreeNode newNode = new PartialFPTreeNode(column);
        newNode.setIndices(indices);
        return newNode;
    }

}

