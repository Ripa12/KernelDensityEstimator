package interval_tree;

import interval_tree.GUI.Histogram;
import interval_tree.GUI.StdDraw;

import java.util.stream.IntStream;

// ToDO: Implement Red-Black tree self-balancing
public class MyIntervalTree {

    public class TreeNode{
        TreeNode left, right;
        NodeData data;

        int max;

        private TreeNode(NodeData data){
            this.data = data;
            this.max = data.getHigh();
            left = right = null;
        }

        @Override
        public String toString(){
            return String.format("Start: " + data.getLow() + " end: " + data.getHigh());
        }
    }

    static abstract class NodeData{
        public int weight;

        NodeData(){
            weight = 1;
        }

        private void incrementWeight(){
            weight += 1;
        }

        abstract int getHigh();
        abstract int getLow();
        abstract boolean equalTo(NodeData other);
        abstract void insertToKernel(UnivariateKernelEstimator e);
        abstract void insertToHistogram(Histogram e, int oldMin, int oldMax);
    }

    public static class Point extends NodeData {
        public int data;

        public Point(int data){
            this.data = data;
        }

        public int getHigh() {
            return data;
        }

        public int getLow() {
            return data;
        }

        boolean equalTo(NodeData other) {
            return other.getLow() == data && other.getHigh() == data;
        }

        @Override
        void insertToKernel(UnivariateKernelEstimator e) {
            e.addValue(data, weight);
        }

        @Override
        void insertToHistogram(Histogram e, int oldMin, int oldMax) {
            e.addDataPoint(scale(data, oldMin, oldMax));
        }

    }

    public static class Interval extends NodeData {
        private static final int BinRange = 500;

        public int start;
        public int end;

        public Interval(int start, int end){
            this.start = start;
            this.end = end;
        }

        public int getHigh() {
            return end;
        }

        public int getLow() {
            return start;
        }

        boolean equalTo(NodeData other) {
            return other.getHigh() == end && other.getLow() == start;
        }

        @Override
        void insertToKernel(UnivariateKernelEstimator e) {
            IntStream.range(start, end).filter(x->x%BinRange==0)
                    .mapToDouble(x->x)
                    .forEach(x->e.addValue(x, weight/1));
//                    .forEach(x->e.addValue(x, weight/100));
        }

        @Override
        void insertToHistogram(Histogram e, int oldMin, int oldMax) {
            IntStream.range(start, end).filter(x->x%BinRange==0)
                    .forEach(x->e.addDataPoint(scale(x, oldMin, oldMax)));
        }

    }


    private Histogram histogram;
    private UnivariateKernelEstimator estimator;
    private TreeNode root;
    private int minVal;
    private int maxVal;
    private int frequency;

    public MyIntervalTree(){
        frequency = 0;
        histogram = null;
        root = null;
        minVal = Integer.MAX_VALUE;
        maxVal = Integer.MIN_VALUE;
    }

    public int getFrequency(){
        return frequency;
    }

    public void insert(NodeData data){
        frequency++;

        minVal = Math.min(minVal, data.getLow());
        maxVal = Math.max(maxVal, data.getHigh());

        TreeNode node = insert(root, data);
        if(this.root == null)
            this.root = node;

    }

    private TreeNode insert(TreeNode root, NodeData data){

        if(root == null)
            return new TreeNode(data);

        int l = root.data.getLow();

        if(data.getLow() < l){
            root.left = insert(root.left, data);
        }
        else if(root.data.equalTo(data)){
            root.data.incrementWeight();
        }
        else{
            root.right = insert(root.right, data);
        }

        root.max = root.max < data.getHigh() ? data.getHigh() : root.max;

        return root;
    }

    public double[][] predictIntervals(double conf){
        return estimator.predictIntervals(conf);
    }

    public void iterate(){
        histogram = new Histogram(MaxNew+1);
        estimator = new UnivariateKernelEstimator(); // ToDo: maybe check if null here to avoid creating a new instance every invocation

        iterate(this.root);

        // display using standard draw
        StdDraw.setCanvasSize(1500, 700);
        histogram.draw(minVal, maxVal);
    }

    private void iterate(TreeNode root)
    {
        if (root == null)
            return;

        iterate(root.left);

        System.out.println(root.toString());

        root.data.insertToKernel(estimator);
        root.data.insertToHistogram(histogram, minVal, maxVal);

        iterate(root.right);
    }

    private final static int MaxNew = 100;
    private final static int MinNew = 0;

    public static int scale(double v, int oldMin, int oldMax){
        final double maxnew = MaxNew;
        final double minnew = MinNew;
        final double maxold = oldMin;
        final double minold = oldMax;

        return (int)(((maxnew-minnew)/(maxold-minold))*(v-maxold)+maxnew);
    }

    public static int rescale(double v, int oldMin, int oldMax){
        final double maxnew = oldMin;
        final double minnew = oldMax;
        final double maxold = MaxNew;
        final double minold = MinNew;

        return (int)(((maxnew-minnew)/(maxold-minold))*(v-maxold)+maxnew);
    }

}
