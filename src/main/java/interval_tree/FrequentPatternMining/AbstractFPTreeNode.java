package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;
import interval_tree.FrequentPatternMining.SupportCount.TableCount;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Richard on 2018-05-19.
 */
public abstract class AbstractFPTreeNode{

    protected List<? extends IIndex> indices;
    private AbstractFPTreeNode parent;
    protected TreeMap<String, AbstractFPTreeNode> children;
    protected int frequency;

    protected String column;

    protected AbstractFPTreeNode(AbstractFPTreeNode parent, String column) {
        this.children = new TreeMap<>();
        indices = new ArrayList<>();
        this.parent = parent;
        this.frequency = 0;

        this.column = column;
    }

    protected abstract AbstractFPTreeNode makeChild(String column);

    public abstract void extractIndexes(String tableName, List<String> columns, TableCount tc);
    public abstract List<IIndex> extractIndexes(double frequency, String tableName, List<String> columns); // ToDo: Could probably be static

    final AbstractFPTreeNode incrementFrequencyOfChild(String name){
        AbstractFPTreeNode temp;
        if (children.containsKey(name)) {
            temp = children.get(name);
        } else {
            temp = makeChild(name); // ToDo: rename makeChild() to makeChild()

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



    final boolean hasChild(String name){
        return children.containsKey(name);
    }
    final int getChildCount(){
        return children.size();
    }

    final int getFrequency(){
        return frequency;
    }


    /// new
    public void addChild(AbstractFPTreeNode child) {
        if(!this.children.containsKey(child.column)) {
            this.children.put(child.column, child);
            child.parent = this;
        }
    }

    final AbstractFPTreeNode getParent(){
        return parent;
    }
    final void setParent(AbstractFPTreeNode parent){
        this.parent = parent;
    }

    final void setFrequency(int freq){
        this.frequency = freq;
    }
    final void incFrequency(){
        this.frequency += 1;
    }


    final TreeMap<String, AbstractFPTreeNode> getChildren(){
        return this.children;
    }

    abstract AbstractFPTreeNode clone(AbstractFPTreeNode other);
}
