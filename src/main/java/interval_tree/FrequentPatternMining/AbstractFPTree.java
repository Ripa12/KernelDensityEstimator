package interval_tree.FrequentPatternMining;

import interval_tree.CandidateIndex.IIndex;
import interval_tree.FrequentPatternMining.SupportCount.TableCount;

import java.util.*;

public abstract class AbstractFPTree {

    /**
     * Member class
     */

    /**
     * Member variables
     */
    private AbstractFPTreeNode root;
    protected HashMap<String, LinkedList<AbstractFPTreeNode>> header;
    protected double minsup;
    protected double totalSupportCount;
//    protected List<IIndex> indices; // ToDo: might not be needed!
    protected String tableName;
    protected TableCount tableCount;


    /**
     * Constructor
     */
    AbstractFPTree(TableCount tableCount, String tableName, AbstractFPTreeNode root) {
        this.tableCount = tableCount;
        this.totalSupportCount = tableCount.getTotalSupportCount();
        this.tableName = tableName;
        this.root = root;
        header = tableCount.buildHeader(tableName);
    }

    protected AbstractFPTreeNode getRoot() {
        return this.root;
    }

//    final public List<IIndex> getIndices() {
//        return indices;
//    }

    public void extractItemSets(double minsup) {
//        indices = new LinkedList<>();
        this.minsup = minsup;

        LinkedList<String> cols = new LinkedList<>();

        extractItemSets(root, cols);
    }

    private void extractItemSets(AbstractFPTreeNode node, LinkedList<String> cols) {

        for (String col : node.children.keySet()) {
            cols.add(col);
            extractItemSets(node.children.get(col), cols);
            cols.removeLast();
        }

        extractItemSet(node, cols);
    }

    abstract void extractItemSet(AbstractFPTreeNode node, List<String> columns);

    // ToDo: FPTreeNode = template
    AbstractFPTreeNode insertTree(Iterator<String> it) {
        AbstractFPTreeNode node = root;

        while (it.hasNext()) {
            String entry = it.next();

            if (node.hasChild(entry)) {
                node = node.getOrCreateChild(entry);
            } else {
                header.get(entry).add(node = node.getOrCreateChild(entry));
            }
        }
        return node;
    }


    // NEW
    public abstract AbstractFPTreeNode createRoot();

    private static final String PATTERN_DELIMITER = ",";

    private void insertFPTree(AbstractFPTreeNode tree, List<AbstractFPTreeNode> words,
                              List<Double> wordValues, Map<String, LinkedList<AbstractFPTreeNode>> headerTable) {
        if (tree.getChildCount() == 0) {
            if (words.size() > 0) {
                AbstractFPTreeNode subTree = tree.makeChild(words.get(0));
                subTree.setFrequency(((int) wordValues.get(0).intValue()));
                if (headerTable.containsKey(words.get(0))) {
                    headerTable.get(words.get(0)).addFirst(subTree);
                } else {
                    LinkedList<AbstractFPTreeNode> newList = new LinkedList<>();
                    newList.add(subTree);
                    headerTable.put(words.get(0).column, newList);
                }
                if (words.size() > 1)
                    insertFPTree(subTree, words.subList(1, words.size()),
                            wordValues.subList(1, wordValues.size()),
                            headerTable);
                tree.addChild(subTree);
            }
        } else {
            for (AbstractFPTreeNode child : tree.getChildren().values()) {
                if (child.column.equals(words.get(0))) {
                    child.incFrequency();
                    if (words.size() > 1)
                        insertFPTree(child, words.subList(1, words.size()),
                                wordValues.subList(1, wordValues.size()),
                                headerTable);
                    return;
                }
            }
            AbstractFPTreeNode newChild = tree.makeChild(words.get(0));
            newChild.setFrequency(((int) wordValues.get(0).intValue()));
            if (headerTable.containsKey(words.get(0))) {
                headerTable.get(words.get(0)).addFirst(newChild);
            } else {
                LinkedList<AbstractFPTreeNode> newList = new LinkedList<>();
                newList.add(newChild);
                headerTable.put(words.get(0).column, newList);
            }
            if (words.size() > 1)
                insertFPTree(newChild, words.subList(1, words.size()),
                        wordValues.subList(1, wordValues.size()), headerTable);
            tree.addChild(newChild);
        }

    }

    public Set<IIndex> findFrequentPatterns(double minsup){
        this.minsup = minsup * totalSupportCount;

        Set<IIndex> frequentPatterns = new HashSet<>();
        Set<String> test = new HashSet<>();
        fpGrowthStep(header, frequentPatterns, test, "");

        System.out.println(test);

        return frequentPatterns;
    }

    private class FPGrowthPair {
        List<AbstractFPTreeNode> treeNodes;
        double frequency;

        private FPGrowthPair(List<AbstractFPTreeNode> t, double f){
            treeNodes = t;
            frequency = f;
        }
    }

    // ToDo: would it be possible to create partial indexes for all possible combinations after FP-Growth, or would that be too many?
    // MIT license
    // https://github.com/PySualk/fp-growth-java/blob/master/src/main/java/org/sualk/fpgrowth/FPgrowth.java
    private void fpGrowthStep(HashMap<String, LinkedList<AbstractFPTreeNode>> headerTable,
                              Set<IIndex> frequentPatterns, Set<String> test,  String base) {

//        boolean isTop = leaves.isEmpty();

        for (String item : headerTable.keySet()) {
            List<AbstractFPTreeNode> treeNodes = headerTable.get(item);

            String currentPattern = item + PATTERN_DELIMITER + base;
            if (currentPattern.endsWith(PATTERN_DELIMITER))
                currentPattern = currentPattern.substring(0,
                        currentPattern.length() - 1);

            // 1. Step: Conditional Pattern Base
            Map<String, FPGrowthPair> conditionalPatternBase = new HashMap<>();

            // Is the item frequent? (count >= minSupport)
            double frequentItemsetCount = 0;

//            if (isTop)
//                leaves.clear();


            // Jump from leaf to leaf
            for (AbstractFPTreeNode treeNode : treeNodes) {// while (treeNode != null) {

//                if(isTop)
//                    leaves.add(treeNode);


                List<AbstractFPTreeNode> nodePattern = new LinkedList<>();
                String conditionalPattern = "";
                frequentItemsetCount += treeNode.getFrequency();
                double supportConditionalPattern = treeNode.getFrequency();

                AbstractFPTreeNode parentNode = treeNode.getParent();

                // Work yourself up to the root
                while (parentNode.parent != null) {
                    conditionalPattern = parentNode.column.concat(
                            PATTERN_DELIMITER + conditionalPattern);
                    nodePattern.add(parentNode.clone(treeNode));
//                    nodePattern.add(parentNode);
                    parentNode = parentNode.getParent();
                }
                if (conditionalPattern.endsWith(PATTERN_DELIMITER))
                    conditionalPattern = conditionalPattern.substring(0,
                            conditionalPattern.length() - 1);

                if (!conditionalPattern.equals("")) {
                    conditionalPatternBase.put(conditionalPattern,
                            new FPGrowthPair(nodePattern, supportConditionalPattern));
                }

            }

            // Is the item frequent? (count >= minSupport)
            if (frequentItemsetCount < minsup) {
                // Skip the current item
                continue;
            } else {
                test.add(currentPattern);

//                AbstractFPTreeNode tempNode = createRoot();
//                frequentPatterns.addAll(tempNode.extractIndexes(frequentItemsetCount, tableName,
//                        Arrays.asList(currentPattern.split(PATTERN_DELIMITER))));
                for (AbstractFPTreeNode treeNode : treeNodes) {
                    frequentPatterns.addAll(treeNode.extractIndexes(frequentItemsetCount, tableName,
                            Arrays.asList(currentPattern.split(PATTERN_DELIMITER))));
                }

            }

            // 2. Step: Conditional FP-Tree
            Map<String, Double> conditionalItemFrequencies = new HashMap<>();
            AbstractFPTreeNode conditionalTree = createRoot();

            for (String conditionalPattern : conditionalPatternBase.keySet()) {
                StringTokenizer tokenizer = new StringTokenizer(
                        conditionalPattern, PATTERN_DELIMITER);

                while (tokenizer.hasMoreTokens()) {
                    String conditionalToken = tokenizer.nextToken();

                    if (conditionalItemFrequencies
                            .containsKey(conditionalToken)) {
                        double count = conditionalItemFrequencies
                                .get(conditionalToken);
                        count += conditionalPatternBase.get(conditionalPattern).frequency;
                        conditionalItemFrequencies.put(conditionalToken, count);
                    } else {
                        conditionalItemFrequencies.put(conditionalToken,
                                conditionalPatternBase.get(conditionalPattern).frequency);
                    }
                }
            }

            // Remove not frequent nodes
            Map<String, Double> tmp = new HashMap<>(conditionalItemFrequencies);
            for (String s : tmp.keySet())
                if (conditionalItemFrequencies.get(s) < minsup)
                    conditionalItemFrequencies.remove(s);

            // Construct Conditional FPTree
            HashMap<String, LinkedList<AbstractFPTreeNode>> conditionalHeaderTable = new HashMap<>();
            for (String conditionalPattern : conditionalPatternBase.keySet()) {
                StringTokenizer tokenizer = new StringTokenizer(
                        conditionalPattern, PATTERN_DELIMITER);
                List<AbstractFPTreeNode> path = new ArrayList<>();
                List<Double> pathValues = new ArrayList<>();

//                while (tokenizer.hasMoreTokens()) {
                for (AbstractFPTreeNode treeNode : conditionalPatternBase
                        .get(conditionalPattern).treeNodes) {

                    String conditionalToken = tokenizer.nextToken();

                    if (conditionalItemFrequencies
                            .containsKey(conditionalToken)) {
                        path.add(treeNode);
                        pathValues.add(conditionalPatternBase
                                .get(conditionalPattern).frequency);

                    }
                }
                if (path.size() > 0) {
                    insertFPTree(conditionalTree, path, pathValues,
                            conditionalHeaderTable);
                }

            }

            if (conditionalTree.getChildCount() > 0)
                fpGrowthStep(conditionalHeaderTable,
                        frequentPatterns, test, currentPattern);
        }
    }
}
