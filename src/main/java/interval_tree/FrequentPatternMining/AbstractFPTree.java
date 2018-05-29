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
    protected List<IIndex> indices;
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

    final public List<IIndex> getIndices() {
        return indices;
    }

    public void extractItemSets(double minsup) {
        indices = new LinkedList<>();
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

    private void insertFPTree(AbstractFPTreeNode tree, List<String> words,
                              List<Double> wordValues, Map<String, LinkedList<AbstractFPTreeNode>> headerTable) {
        if (tree.getChildCount() == 0) {
            if (words.size() > 0) {
                AbstractFPTreeNode subTree = tree.clone(words.get(0));//new FPtree(words.get(0), tree);
//                subTree.setParent(tree);
                subTree.setFrequency(wordValues.get(0).intValue());
                if (headerTable.containsKey(words.get(0))) {
                    headerTable.get(words.get(0)).addFirst(subTree);
//                    subTree.setNext(headerTable.get(words.get(0)));
//                    headerTable.replace(words.get(0), subTree);
                } else {
                    LinkedList<AbstractFPTreeNode> newList = new LinkedList<>();
                    newList.add(subTree);
                    headerTable.put(words.get(0), newList);
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
            AbstractFPTreeNode newChild = tree.clone(words.get(0));//new FPtree(words.get(0), tree);
//            newChild.setParent(tree);
            newChild.setFrequency(wordValues.get(0).intValue());
            if (headerTable.containsKey(words.get(0))) {
                headerTable.get(words.get(0)).addFirst(newChild);
//                newChild.setNext(headerTable.get(words.get(0)));
//                headerTable.replace(words.get(0), newChild);
            } else {
//                headerTable.put(words.get(0), newChild);
                LinkedList<AbstractFPTreeNode> newList = new LinkedList<>();
                newList.add(newChild);
                headerTable.put(words.get(0), newList);
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

    // MIT license
    // https://github.com/PySualk/fp-growth-java/blob/master/src/main/java/org/sualk/fpgrowth/FPgrowth.java
    private void fpGrowthStep(HashMap<String, LinkedList<AbstractFPTreeNode>> headerTable,
                              Set<IIndex> frequentPatterns, Set<String> test,  String base) {

        for (String item : headerTable.keySet()) {
            List<AbstractFPTreeNode> treeNodes = headerTable.get(item);

            String currentPattern = item + PATTERN_DELIMITER + base;
            if (currentPattern.endsWith(PATTERN_DELIMITER))
                currentPattern = currentPattern.substring(0,
                        currentPattern.length() - 1);

//            log.debug("=============================================");
//            log.debug("Start Mining Rules for {}", currentPattern);

            // 1. Step: Conditional Pattern Base
            Map<String, Double> conditionalPatternBase = new HashMap<>();

            // Is the item frequent? (count >= minSupport)
            double frequentItemsetCount = 0;

            AbstractFPTreeNode combinedTreeNode = createRoot();

            // Jump from leaf to leaf
            for (AbstractFPTreeNode treeNode : treeNodes) {// while (treeNode != null) {

                combinedTreeNode.combineNode(treeNode);

                String conditionalPattern = "";
                frequentItemsetCount += treeNode.getFrequency();
                double supportConditionalPattern = treeNode.getFrequency();

                AbstractFPTreeNode parentNode = treeNode.getParent();

                // Work yourself up to the root
                while (parentNode.parent != null) { // while (!parentNode.isRoot()) {
                    conditionalPattern = parentNode.column.concat(
                            PATTERN_DELIMITER + conditionalPattern);
                    parentNode = parentNode.getParent();
                }
                if (conditionalPattern.endsWith(PATTERN_DELIMITER))
                    conditionalPattern = conditionalPattern.substring(0,
                            conditionalPattern.length() - 1);

//                    treeNode = treeNode.getNext();

                if (!conditionalPattern.equals(""))
                    conditionalPatternBase.put(conditionalPattern,
                            supportConditionalPattern);

            }

            // Is the item frequent? (count >= minSupport)
            if (frequentItemsetCount < minsup) {
                // Skip the current item
//                log.debug("Refused Item Set: {} ({})", currentPattern,
//                        frequentItemsetCount);
                continue;
            } else {
//                log.debug("Frequent Item Set {}, ({}) found", currentPattern,
//                        frequentItemsetCount);
//                frequentPatterns.add(new FrequentPattern(currentPattern,
//                        frequentItemsetCount, (double) frequentItemsetCount
//                        / transactionCount));
                test.add(currentPattern);

                frequentPatterns.addAll(combinedTreeNode.extractIndexes(frequentItemsetCount, tableName,
                        Arrays.asList(currentPattern.split(PATTERN_DELIMITER))));

//                frequentPatterns.addAll(combinedTreeNode.extractIndexes(tableName,
//                        Arrays.asList(currentPattern.split(PATTERN_DELIMITER))));
            }

//            log.debug("ConditionalPatternBase: {}", conditionalPatternBase);

            // 2. Step: Conditional FP-Tree
            Map<String, Double> conditionalItemFrequencies = new HashMap<>();
            AbstractFPTreeNode conditionalTree = createRoot();//new FPtree("null", null);
//            conditionalTree.setRoot(Boolean.TRUE);

            for (String conditionalPattern : conditionalPatternBase.keySet()) {
                StringTokenizer tokenizer = new StringTokenizer(
                        conditionalPattern, PATTERN_DELIMITER);

//                AbstractFPTreeNode tempRoot = root;

                while (tokenizer.hasMoreTokens()) {
                    String conditionalToken = tokenizer.nextToken();

//                    tempRoot = tempRoot.getChild(conditionalToken);

                    if (conditionalItemFrequencies
                            .containsKey(conditionalToken)) {
                        double count = conditionalItemFrequencies
                                .get(conditionalToken);
                        count += conditionalPatternBase.get(conditionalPattern);
                        conditionalItemFrequencies.put(conditionalToken, count);
                    } else {
                        conditionalItemFrequencies.put(conditionalToken,
                                conditionalPatternBase.get(conditionalPattern));
                    }
                }
            }

            // Remove not frequent nodes
            Map<String, Double> tmp = new HashMap<>(conditionalItemFrequencies);
            for (String s : tmp.keySet())
                if (conditionalItemFrequencies.get(s) < minsup)
                    conditionalItemFrequencies.remove(s);

//            log.debug("ConditionalItemFrequencies: {}",
//                    conditionalItemFrequencies);

            // Construct Conditional FPTree
            HashMap<String, LinkedList<AbstractFPTreeNode>> conditionalHeaderTable = new HashMap<>();
            for (String conditionalPattern : conditionalPatternBase.keySet()) {
                StringTokenizer tokenizer = new StringTokenizer(
                        conditionalPattern, PATTERN_DELIMITER);
                List<String> path = new ArrayList<>();
                List<Double> pathValues = new ArrayList<>();

                AbstractFPTreeNode tempRoot = root; // My Code

                while (tokenizer.hasMoreTokens()) {
                    String conditionalToken = tokenizer.nextToken();

                    tempRoot = tempRoot.getChild(conditionalToken);  // My Code

                    if (conditionalItemFrequencies
                            .containsKey(conditionalToken)) {
                        path.add(conditionalToken);
                        pathValues.add(conditionalPatternBase
                                .get(conditionalPattern));

                    }
                }
                if (path.size() > 0) {
                    insertFPTree(conditionalTree, path, pathValues,
                            conditionalHeaderTable);
                }

            }

//            log.debug("End Mining Rules for {}", currentPattern);

//            if (!conditionalTree.getChildren().isEmpty())
            if (conditionalTree.getChildCount() > 0)
                fpGrowthStep(conditionalHeaderTable,
                        frequentPatterns, test, currentPattern);
        }
    }
}
