package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.FullIndex;
import interval_tree.CandidateIndex.IIndex;
import interval_tree.FrequentPatternMining.SupportCount.TableProperties;

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
    public List<IIndex> extractIndexes(double frequency, String tableName, List<String> columns) {
        return Collections
                .singletonList(new FullIndex(frequency, 0, tableName, String.join(",", columns)));
    }

    @Override
    AbstractFPTreeNode clone(AbstractFPTreeNode other) {
        return new FullFPTreeNode(null, column);
    }

    @Override
    protected AbstractFPTreeNode makeChild(String col) {
        return new FullFPTreeNode(this, col);
    }
}
