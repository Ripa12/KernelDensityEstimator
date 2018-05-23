package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Richard on 2018-05-19.
 */
public abstract class AbstractFPTreeNode{

    protected AbstractFPTreeNode parent;
    protected TreeMap<String, AbstractFPTreeNode> children;
    protected int frequency;

    protected AbstractFPTreeNode(AbstractFPTreeNode parent) {
        this.children = new TreeMap<>();
        this.parent = parent;
        this.frequency = 0;
    }

    protected abstract AbstractFPTreeNode clone();
    public abstract List<IIndex> extractIndexes(String tableName, List<String> columns);

    final AbstractFPTreeNode getOrCreateChild(String name){
        AbstractFPTreeNode temp;
        if (children.containsKey(name)) {
            temp = children.get(name);
        } else {
            temp = clone(); // ToDo: rename clone() to makeChild()

            children.put(name, temp);
        }
        temp.frequency++;
        return temp;
    }

    final AbstractFPTreeNode getChild(String name){
        AbstractFPTreeNode temp = null;
        if (children.containsKey(name)) {
            temp = children.get(name);
        }
        return temp;
    }

    final int getFrequency(){
        return frequency;
    }
}
