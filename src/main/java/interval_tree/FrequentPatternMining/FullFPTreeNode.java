package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.FullIndex;
import interval_tree.CandidateIndex.IIndex;
import interval_tree.FrequentPatternMining.SupportCount.TableCount;
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
    public List<? extends IIndex> extractIndexes(String tableName, List<String> columns, TableCount tc){
        return Collections
                .singletonList(new FullIndex(frequency, 0, tableName, String.join(",", columns)));
    }

    @Override
    public List<IIndex> extractIndexes(double frequency, String tableName, List<String> columns) {
        return Collections
                .singletonList(new FullIndex(frequency, 0, tableName, String.join(",", columns)));
    }

    @Override
    void combineNode(AbstractFPTreeNode other) {
        this.frequency += other.frequency;
    }

    @Override
    protected AbstractFPTreeNode makeChild(String col) {
        return new FullFPTreeNode(this, col);
    }

    @Override
    protected AbstractFPTreeNode makeChild(AbstractFPTreeNode other) {
        return new FullFPTreeNode(this, other.column);
    }

    @Override
    protected AbstractFPTreeNode cloneRoot() {
        return new FullFPTreeNode(null, "");
    }
}
