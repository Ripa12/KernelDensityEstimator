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

    FullFPTreeNode(FullFPTreeNode parent) {
        super(parent);

    }

    @Override
    public List<IIndex> extractIndexes(List<String> columns){
        return Collections
                .singletonList(new FullIndex(frequency, 0, String.join(",", columns)));
    }

    @Override
    protected FullFPTreeNode clone() {
        return new FullFPTreeNode(this);
    }
}
