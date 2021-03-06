package Indexer.FrequentPatternMining.Full;

import Indexer.CandidateIndex.FullIndex;
import Indexer.CandidateIndex.IIndex;
import Indexer.FrequentPatternMining.AbstractFPTreeNode;

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
    public AbstractFPTreeNode doClone(AbstractFPTreeNode other, List<? extends IIndex> indices) {
        return new FullFPTreeNode(null, column);
    }

    @Override
    protected AbstractFPTreeNode makeChild(String col) {
        return new FullFPTreeNode(this, col);
    }
}
