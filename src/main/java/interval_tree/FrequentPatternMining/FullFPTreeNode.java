package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.FullIndex;
import interval_tree.CandidateIndex.IIndex;
import interval_tree.SubspaceClustering.Clique;
import interval_tree.SubspaceClustering.MyVector;

import java.util.Collections;
import java.util.List;

/**
 * Created by Richard on 2018-05-19.
 */
public class FullFPTreeNode extends AbstractFPTreeNode {

    FullFPTreeNode(FullFPTreeNode parent, String column) {
        super(parent, column);
    }

    @Override
    public List<IIndex> extractIndexes(String tableName, List<String> columns){
        return Collections
                .singletonList(new FullIndex(frequency, 0, tableName, String.join(",", columns)));
    }

    @Override
    void combineNode(AbstractFPTreeNode other) {
        this.frequency += other.frequency;
    }

    @Override
    protected FullFPTreeNode clone(String col) {
        return new FullFPTreeNode(this, col);
    }

    @Override
    protected AbstractFPTreeNode cloneRoot() {
        return new FullFPTreeNode(null, "");
    }
}
